---
description: "Use when starting a new feature, HU, or onboarding to the optimizador-envios project. Contains full project context: architecture, domain model, tech stack, SOLID status, completed HUs, and all conventions required for HU-03 onward."
---

# Optimizador de Envíos — Project Context

## What This Product Does

A logistics optimizer that recommends the best shipping provider (FedEx, DHL, local) for shipments **within Colombia**, based on a user-defined priority. The user enters origin, destination, weight, and priority (cost or time), and the system recommends the best provider.

**MVP providers (mocked — no real API integration):** FedEx, DHL, one local Colombian carrier.

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| React | 19 | UI framework |
| TypeScript | 5 | Strict typing throughout |
| Vite | Latest | Build tool |
| Zustand | 5 | Global state management |
| Vitest | 3 | Unit + integration testing |
| @testing-library/react | 16 | Component testing |
| jsdom | — | Test environment |
| TailwindCSS | — | Styling |

---

## Architecture: Hexagonal (Ports & Adapters)

```
src/
├── domain/             ← Pure types, constants, validation logic. NO React, NO API, NO hooks.
│   └── order.ts
├── application/        ← Orchestration. Can use domain. Cannot import UI or infra directly.
│   ├── hooks/
│   │   ├── useOrder.ts         ← Exposes order state + actions to UI
│   │   ├── useAutocomplete.ts  ← Debounced location search
│   │   └── useDebounce.ts      ← Generic debounce utility
│   ├── services/
│   │   └── orderService.ts     ← Pure orchestrator (DIP: injects store actions as params)
│   └── store/
│       └── orderStore.ts       ← Zustand store (order state)
├── infrastructure/     ← HTTP calls ONLY here. No business logic.
│   └── api/
│       └── openRouteService.ts ← Geocoding adapter (OpenRouteService)
└── ui/                 ← React components. Use hooks/store. NEVER call API directly.
    ├── components/
    │   ├── OrderForm.tsx        ← Smart form (origin, destination, weight)
    │   ├── PrioritySelector.tsx ← Priority selection screen (COST / TIME)
    │   ├── LocationInput.tsx    ← Autocomplete input for location fields
    │   └── WeightInput.tsx      ← Number input + unit selector
    └── pages/
        └── OrderPage.tsx        ← Step orchestrator (form → priority)
```

**Layer dependency rule (MUST follow this order):**
1. `domain` → 2. `application/store` → 3. `application/hooks` → 4. `application/services` → 5. `infrastructure/api` → 6. `ui/components` → 7. `ui/pages`

Do NOT add new folders without explicit user approval.

---

## Domain Model (`src/domain/order.ts`)

```ts
export type Location = {
  name: string
  lat: number
  lng: number
}

export const WEIGHT_UNIT = { GRAMS: 'GRAMS', KILOGRAMS: 'KILOGRAMS', POUNDS: 'POUNDS' } as const
export type WeightUnit = keyof typeof WEIGHT_UNIT

export const SHIPPING_PRIORITY = { COST: 'COST', TIME: 'TIME' } as const
export type ShippingPriority = keyof typeof SHIPPING_PRIORITY

export type Order = {
  origin?: Location
  destination?: Location
  weight?: number
  weightUnit?: WeightUnit
  priority?: ShippingPriority   // Set after PrioritySelector step. Used in HU-03 backend payload.
}

export type ValidationResult = {
  valid: boolean
  errors: string[]
}
```

**Weight limits:** 0.001 Kg minimum, 70 Kg maximum. Validated in `validateOrder()`.  
**Geographic coverage:** Colombia only (enforced at infrastructure via `boundary.country=CO`).

---

## Application State (`src/application/store/orderStore.ts`)

Zustand store — single source of truth for the order:

```ts
type OrderState = {
  order: Order | null
  setOrder: (order: Order) => void
  setPriority: (priority: ShippingPriority) => void  // Writes priority INSIDE order
  clearOrder: () => void
}
```

**Important:** `priority` is stored as `order.priority`, NOT as a separate top-level field.  
`setPriority` does: `{ order: { ...state.order, priority } }`.

---

## Application Hook (`src/application/hooks/useOrder.ts`)

The hook UI components use to interact with the order state:

```ts
useOrder(): {
  order: Order | null
  priority: ShippingPriority | null   // Reads order?.priority ?? null
  submitOrder: (order: Order) => ValidationResult
  clearOrder: () => void
  setPriority: (p: ShippingPriority) => void
}
```

---

## Application Service (`src/application/services/orderService.ts`)

DIP applied — store actions are injected as parameters (service does NOT import the store):

```ts
submitOrderService(order: Order, setOrder: (o: Order) => void): ValidationResult
clearOrderService(clearOrder: () => void): void
```

---

## Multi-Step UI Flow

```
OrderPage (step state: 'form' | 'priority')
  ├── step === 'form'     → <OrderForm onSuccess={() => setStep('priority')} />
  └── step === 'priority' → <PrioritySelector onConfirm={setPriority} />
```

After `PrioritySelector` confirms, `order.priority` is set in the store. The order object is now complete and ready to be sent to the backend in HU-03.

---

## Business Rules Implemented

| Rule | Status | Where |
|---|---|---|
| R1: Colombia only | ✅ | `openRouteService.ts` (`boundary.country=CO`) |
| R2: Origin, destination, weight required | ✅ | `validateOrder()` in `domain/order.ts` |
| R3: Weight 0.001–70 Kg | ✅ | `validateOrder()` + `toKilograms()` |
| R4: Priority required before recommendation | ✅ | `PrioritySelector` — confirm button disabled until selection |

## Business Rules Pending (HU-03+)

| Rule | HU | Description |
|---|---|---|
| R5 | HU-03 | Priority COST → recommend lowest cost option |
| R6 | HU-03 | Priority TIME → recommend fastest option |
| R7 | HU-03 | Cost tie → recommend lowest time among tied |
| R8 | HU-03 | Time tie → recommend lowest cost among tied |
| R9 | HU-04 | Show alternative options besides main recommendation |
| R10 | HU-05 | User must select a provider to continue |
| R11 | HU-05 | Persist order with selected provider |

---

## TDD Convention (STRICT — DO NOT SKIP)

Every feature follows this exact cycle, **one phase at a time**, committing between each:

```
RED   → Write failing tests first. Run them. Confirm they fail.
GREEN → Write minimum code to make tests pass.
REFACTOR → Apply SOLID. Clean up. Run tests again. Confirm still green.
```

**Commit convention:**
```
test(layer): description 🔴       ← RED phase
feat(layer): description 🟢       ← GREEN phase
refactor(layer): description 🔵   ← REFACTOR phase
```

**Layers for commit scope:** `domain`, `application`, `infrastructure`, `ui`

**User workflow:** User runs `npm test` between each phase and confirms results before proceeding.

---

## Test Structure

```
tests/
├── setup.ts                         ← @testing-library/react setup
├── domain/
│   └── order.spec.ts                ← Pure domain logic tests
├── application/
│   ├── orderStore.spec.ts           ← Zustand store tests
│   ├── useOrder.spec.ts             ← Application hook tests
│   └── useAutocomplete.spec.ts      ← Debounced autocomplete hook tests
├── infrastructure/
│   ├── openRouteService.unit.spec.ts ← Pure helper functions (no HTTP)
│   └── openRouteService.spec.ts     ← Integration tests (skip unless env var set)
└── ui/
    ├── OrderForm.spec.tsx
    ├── PrioritySelector.spec.tsx
    └── OrderPage.spec.tsx
```

**Mocking rules:**
- UI tests mock `useOrder` completely (include `priority` and `setPriority` in mock)
- Application hook tests mock `useOrderStore` via real store + `beforeEach clearOrder()`
- Infrastructure unit tests: no mocks, pure functions only
- Integration tests: skipped unless `RUN_OPENROUTE_INTEGRATION=true`

**tsconfig:** `tests/` is included in `tsconfig.app.json` → `"include": ["src", "tests"]`

---

## SOLID Status by Layer

| Layer | Violations |
|---|---|
| `domain/order.ts` | None ✅ |
| `application/store` | ISP minor: `OrderState` mixes state + actions in one type |
| `application/services` | None ✅ (DIP applied — store injected as param) |
| `application/hooks/useOrder` | ISP minor: hook returns full API to all consumers |
| `application/hooks/useAutocomplete` | None ✅ |
| `infrastructure/openRouteService` | DIP: no abstract port for geocoding adapter |
| `ui/LocationInput` | DIP: `useAutocomplete` not injectable |
| `ui/WeightInput` | None ✅ |
| `ui/OrderForm` | DIP: `useOrder` not injectable |
| `ui/PrioritySelector` | None ✅ — cleanest UI component |
| `ui/OrderPage` | DIP: `useOrder` not injectable |

---

## Completed User Stories

### HU-01 ✅ — Register Shipping Order
User enters origin, destination, and weight. System validates and stores the order.

### HU-02 ✅ — Define Shipping Priority
User selects COST or TIME priority after form submission. Priority is stored inside `order.priority`.

---

## What HU-03 Needs

HU-03 is: **"Obtain main provider recommendation"**

The frontend will call a backend endpoint with the complete `Order` object (including `order.priority`) and receive back a recommendation. The recommendation logic (Rules R5–R8) lives in the backend.

**New things needed for HU-03:**

1. **Domain:** New types `ShippingOption` and `Recommendation` (or similar)
2. **Infrastructure:** New API adapter to call the backend recommendation endpoint (POST or GET with order payload)
3. **Application:** New service/hook to fetch recommendation
4. **UI:** New component to display the recommended provider (name, cost, estimated time)
5. **OrderPage:** Add step `'recommendation'` after `'priority'`

**The `order` object that will be sent to the backend:**
```ts
{
  origin:      { name: string, lat: number, lng: number },
  destination: { name: string, lat: number, lng: number },
  weight:      number,
  weightUnit:  'KILOGRAMS' | 'GRAMS' | 'POUNDS',
  priority:    'COST' | 'TIME'
}
```

---

## Key Conventions

- `import type` for all type-only imports (enforced by `verbatimModuleSyntax: true`)
- No `require()` — ES modules only
- No `import React from 'react'` — React 19 new JSX transform; not needed
- `data-testid` follows pattern: `option-cost`, `option-time`, `input-origin`, `input-destination`, `input-weight`
- `describe` blocks labeled by HU: `describe('ComponentName (HU-01)')`, `describe('ComponentName (HU-02)')`
- Do NOT add docstrings or comments to code you didn't change
- Do NOT batch RED + GREEN — strict phase separation enforced by user
