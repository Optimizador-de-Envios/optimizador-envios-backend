# Context HU-05 Instructions

## Purpose

Implement the backend endpoint for HU-05: select and confirm provider.

This endpoint is the persistence step that happens after the user:

1. registers a valid shipment
2. selects a shipping priority
3. receives a recommendation and alternatives
4. selects one provider
5. confirms that provider

The frontend for that flow already exists. What is missing is the real backend endpoint that persists the order together with the selected provider in the database.

## Functional Context

### PRD Alignment

Relevant PRD rules:

- Rule 10: the user must select a provider to continue with the process
- Rule 11: the system must persist the order information when the user selects and confirms a provider

### User Story Alignment

HU-05: Seleccionar y confirmar proveedor

Business goal:

- allow the user to choose one provider from the available options
- persist the shipment together with that chosen provider
- continue the process once persistence succeeds

Acceptance criteria covered by this endpoint:

1. Happy path
   - Given the system displayed the recommended option and the available alternatives
   - When the user selects the desired provider
   - Then the system must continue with the order process

2. No provider selected
   - Given the system displayed the recommended option and the available alternatives
   - When the user tries to continue without selecting a provider
   - Then the system must not continue
   - And it must indicate that a provider must be selected

3. Persistence on confirmation
   - Given the system displayed the recommended option and the available alternatives
   - When the user selects a provider and confirms that selection
   - Then the system must persist the order information with the selected provider
   - And it must allow the process to continue

## Endpoint To Implement

```http
POST /api/v1/pedido/confirmar
```

### Headers

```http
Content-Type: application/json
```

## Request Contract

The frontend sends this exact JSON structure:

```json
{
  "order": {
    "origin": {
      "name": "Tunja, BY, Colombia",
      "lat": 5.53528,
      "lng": -73.36778
    },
    "destination": {
      "name": "Bogotá, DC, Colombia",
      "lat": 4.635456,
      "lng": -74.08768
    },
    "weight": 10,
    "weightUnit": "KILOGRAMS",
    "priority": "COST"
  },
  "selectedOption": {
    "providerName": "Local",
    "cost": 30386.59,
    "currency": "COP",
    "estimatedDays": 1
  }
}
```

## Request Field Semantics

### order

Represents the shipment the user already completed in the previous steps.

- `origin`: origin location with human-readable name and coordinates
- `destination`: destination location with human-readable name and coordinates
- `weight`: numeric package weight
- `weightUnit`: current frontend enum values are `GRAMS`, `KILOGRAMS`, `POUNDS`
- `priority`: current frontend enum values are `COST`, `TIME`

### selectedOption

Represents the provider the user explicitly selected on the results screen.

- `providerName`: provider label shown to the user, for example `FedEx`, `DHL`, or `Local`
- `cost`: quoted shipping price shown to the user
- `currency`: currently expected as `COP`
- `estimatedDays`: quoted delivery time shown to the user

## Backend Responsibility

The backend must treat this request as the final provider confirmation step.

Expected behavior:

1. validate that `order` exists
2. validate that `selectedOption` exists
3. validate that the order is complete
4. persist the confirmed order and the selected provider in the database
5. return the saved confirmation as JSON

## Validation Expectations

At minimum, the backend should validate:

- `order.origin` exists
- `order.destination` exists
- `order.weight` exists and is numeric
- `order.weightUnit` exists
- `order.priority` exists
- `selectedOption.providerName` exists
- `selectedOption.cost` exists and is numeric
- `selectedOption.currency` exists
- `selectedOption.estimatedDays` exists and is numeric

If any of those are missing or invalid, the backend should reject the request with a non-2xx status.

## Success Response Contract

The frontend expects a JSON response with this shape:

```json
{
  "id": "abc-123",
  "origin": {
    "name": "Tunja, BY, Colombia",
    "lat": 5.53528,
    "lng": -73.36778
  },
  "destination": {
    "name": "Bogotá, DC, Colombia",
    "lat": 4.635456,
    "lng": -74.08768
  },
  "weight": 10,
  "weightUnit": "KILOGRAMS",
  "priority": "COST",
  "distanceKm": 148.3,
  "selectedOption": {
    "providerName": "Local",
    "cost": 30386.59,
    "currency": "COP",
    "estimatedDays": 1
  }
}
```

## Response Field Semantics

- `id`: identifier of the persisted confirmed order
- `origin`: persisted origin data
- `destination`: persisted destination data
- `weight`: persisted package weight
- `weightUnit`: persisted weight unit
- `priority`: persisted selected priority
- `distanceKm`: distance associated with the shipment and returned by backend
- `selectedOption`: provider that was actually persisted as selected

The frontend uses this response as the source of truth to render the confirmation page.

## Recommended HTTP Status Codes

- `201 Created` when the confirmed order is successfully persisted
- `200 OK` is acceptable if that matches backend conventions
- `400 Bad Request` for malformed payload
- `422 Unprocessable Entity` for business validation failures
- `500 Internal Server Error` for unexpected persistence errors

## Frontend Expectations On Failure

The frontend considers any non-2xx response to be an error.

Current behavior:

- on success: stores the confirmation response and navigates to `/confirmation`
- on failure: stores the error state and shows an error screen

Because of that, the backend should return a proper non-2xx status whenever the confirmation cannot be persisted.

## Current Frontend Integration

The frontend already does the following:

- sends `POST /api/v1/pedido/confirmar`
- sends `{ order, selectedOption }` in the request body
- expects a JSON response with the saved confirmation
- navigates to `/confirmation` on success
- shows an error state on failure

So the missing work is backend-only: validation, persistence, and returning the expected response DTO.

## Important Compatibility Notes

1. Enum casing matters
   - `weightUnit`: `GRAMS`, `KILOGRAMS`, `POUNDS`
   - `priority`: `COST`, `TIME`

2. Response body is required
   - do not return an empty body on success
   - the frontend expects a confirmation object

3. `distanceKm` is expected in the response
   - if it is not directly stored, compute or hydrate it before returning

4. CORS may be necessary
   - the frontend calls this endpoint from the browser

## Suggested Backend Mental Model

```text
confirm provider selection
-> validate payload
-> validate business rules
-> persist confirmed order + selected provider
-> generate or return confirmation id
-> return saved confirmation DTO
```

## Copy-Ready Prompt For Another AI

```text
Implement the backend endpoint for HU-05 of the shipping optimizer.

Business context:
- The user already created a shipment, selected a priority, received provider options, selected one provider, and clicked confirm.
- This endpoint is the persistence step required by PRD Rule 11 and HU-05.
- The endpoint must save the shipment together with the selected provider.

Endpoint:
POST /api/v1/pedido/confirmar
Content-Type: application/json

Request body shape:
{
  "order": {
    "origin": { "name": string, "lat": number, "lng": number },
    "destination": { "name": string, "lat": number, "lng": number },
    "weight": number,
    "weightUnit": "GRAMS" | "KILOGRAMS" | "POUNDS",
    "priority": "COST" | "TIME"
  },
  "selectedOption": {
    "providerName": string,
    "cost": number,
    "currency": string,
    "estimatedDays": number
  }
}

Success response shape expected by frontend:
{
  "id": string,
  "origin": { "name": string, "lat": number, "lng": number },
  "destination": { "name": string, "lat": number, "lng": number },
  "weight": number,
  "weightUnit": string,
  "priority": string,
  "distanceKm": number,
  "selectedOption": {
    "providerName": string,
    "cost": number,
    "currency": string,
    "estimatedDays": number
  }
}

Behavior required:
- validate that order and selectedOption exist
- validate required order fields: origin, destination, weight, weightUnit, priority
- persist the confirmed order with the selected provider
- return the saved confirmation DTO as JSON
- return non-2xx for validation or persistence errors

Recommended statuses:
- 201 Created on success
- 400 or 422 on invalid payload
- 500 on unexpected server/database failure

Important:
- The frontend already calls this endpoint and expects JSON in the response
- The frontend will navigate to /confirmation on success
- The frontend will show an error state on non-2xx
```