# HU-03 Backend — Contexto de Implementación

## Qué construye esta HU

Dado un pedido con origen, destino, peso y prioridad, el backend calcula cotizaciones de 3 proveedores (FedEx, DHL, Local), aplica el motor de recomendación con las reglas R5–R8, y devuelve la opción recomendada más las alternativas disponibles.

---

## Endpoint

```
POST /api/v1/pedido
```

### Request Body

```json
{
  "order": {
    "origin":      { "name": "Tunja, BY, Colombia", "lat": 5.53528,   "lng": -73.36778 },
    "destination": { "name": "Bogotá, DC, Colombia", "lat": 4.635456, "lng": -74.08768 },
    "weight":      5,
    "weightUnit":  "KILOGRAMS",
    "priority":    "TIME"
  }
}
```

### Response Body

```json
{
  "recommendation": {
    "providerName":  "DHL",
    "cost":          22500,
    "currency":      "COP",
    "estimatedDays": 1
  },
  "alternatives": [
    { "providerName": "FedEx", "cost": 28000, "currency": "COP", "estimatedDays": 1 },
    { "providerName": "Local", "cost": 18000, "currency": "COP", "estimatedDays": 2 }
  ]
}
```

> **Nota HU-04:** `alternatives` se popula en este mismo endpoint. Si no hay alternativas, devuelve lista vacía.  
> **Nota HU-05:** El mismo endpoint se extiende a `POST /api/v1/pedido/seleccionar` para persistir la selección del usuario.

---

## Modelo de Cálculo

### Configuración de proveedores (`application.yml`)

```yaml
proveedores:
  fedex:
    costoBase: 10000
    precioPorKg: 5000
    precioPorKm: 200
    kmPorDia: 800
  dhl:
    costoBase: 12000
    precioPorKg: 4500
    precioPorKm: 250
    kmPorDia: 900
  local:
    costoBase: 8000
    precioPorKg: 4000
    precioPorKm: 150
    kmPorDia: 600
```

### Fórmulas

```
Costo (COP) = costoBase + (pesoKg × precioPorKg) + (distanciaKm × precioPorKm)
Tiempo (días) = ceil(distanciaKm / kmPorDia)
```

### Conversión de unidades de peso

| Entrada      | Conversión a KG     |
|---|---|
| `KILOGRAMS`  | sin conversión      |
| `GRAMS`      | valor / 1000        |
| `POUNDS`     | valor × 0.453592    |

---

## Distancia

Calculada con **OpenRouteService** `/v2/directions/driving-car`.

- **Parámetros:** `start=lng,lat` (origen) y `end=lng,lat` (destino)
- **Campo usado:** `features[0].properties.summary.distance` → valor en **metros** → dividir por 1000 para obtener km

```
GET https://api.openrouteservice.org/v2/directions/driving-car
  ?api_key=YOUR_KEY
  &start=-73.36778,5.53528
  &end=-74.08768,4.635456
```

---

## Arquitectura (Hexagonal)

```
domain/
  model/
    Pedido.java           ← origen, destino, peso, unidad, prioridad
    Ubicacion.java        ← nombre, lat, lng
    Cotizacion.java       ← proveedor, costo, diasEntrega
    Recomendacion.java    ← Cotizacion recomendada + List<Cotizacion> alternativas
  valueobject/
    Prioridad.java        ← enum: COST, TIME
    UnidadPeso.java       ← enum: KILOGRAMS, GRAMS, POUNDS
  service/
    MotorRecomendacionService.java   ← aplica Strategy R5–R8
  exception/
    PedidoInvalidoException.java

application/
  port/
    input/
      ObtenerRecomendacionUseCase.java   ← interface
    output/
      ProveedorClient.java              ← interface (port hacia proveedores)
      DistanciaClient.java              ← interface (port hacia ORS)
  usecase/
    ObtenerRecomendacionUseCaseImpl.java ← orquesta: obtiene distancia → llama proveedores → aplica motor

infrastructure/
  adapter/
    input/
      rest/
        PedidoController.java           ← POST /api/v1/pedido
        dto/
          PedidoRequestDto.java
          OrderDto.java
          UbicacionDto.java
          RecomendacionResponseDto.java
          CotizacionDto.java
    output/
      external/
        FedexClientMock.java            ← implementa ProveedorClient
        DhlClientMock.java              ← implementa ProveedorClient
        LocalClientMock.java            ← implementa ProveedorClient
        OpenRouteServiceAdapter.java    ← implementa DistanciaClient
  mapper/
    PedidoMapper.java                   ← DTO ↔ Domain

config/
  ProvedorProperties.java               ← @ConfigurationProperties("proveedores")
  ProveedorConfig.java                  ← costoBase, precioPorKg, precioPorKm, kmPorDia
  exception/
    GlobalExceptionHandler.java         ← @RestControllerAdvice
```

---

## Patrones de Diseño

### Strategy — Motor de Recomendación

```
EstrategiaRecomendacion (interface)
  └── seleccionar(List<Cotizacion>) → Cotizacion
       ↑
  EstrategiaCosto     ← ordena por costo ASC, desempate por diasEntrega ASC (R5, R7)
  EstrategiaTiempo    ← ordena por diasEntrega ASC, desempate por costo ASC (R6, R8)
```

### Factory — Selección de Estrategia

```
EstrategiaFactory
  └── obtener(Prioridad) → EstrategiaRecomendacion
```

### Adapter — Proveedores Simulados

```
ProveedorClient (interface)
  └── cotizar(Pedido, double distanciaKm) → Cotizacion
       ↑
  FedexClientMock
  DhlClientMock
  LocalClientMock
```

---

## Reglas de Negocio (R5–R8)

| Regla | Descripción |
|---|---|
| R5 | Prioridad `COST` → recomendar la cotización de menor costo |
| R6 | Prioridad `TIME` → recomendar la cotización de menor tiempo |
| R7 | Empate en costo → desempatar por menor `diasEntrega` |
| R8 | Empate en tiempo → desempatar por menor `costo` |

Las alternativas son todas las cotizaciones **excepto** la recomendada.

---

## Reglas de Validación del Pedido

| Campo | Regla |
|---|---|
| `origin` | No nulo |
| `destination` | No nulo |
| `weight` | No nulo, > 0 |
| `weightUnit` | No nulo, valor en {KILOGRAMS, GRAMS, POUNDS} |
| `priority` | No nulo, valor en {COST, TIME} |
| Peso normalizado a KG | ≥ 0.001 y ≤ 70 |

Violación → `PedidoInvalidoException` → HTTP 400.

---

## Ciclo TDD por Capa

```
🔴 RED    → test(domain):          MotorRecomendacionService — reglas R5–R8
🟢 GREEN  → feat(domain):          implementación mínima del motor
🔵 REFACTOR → refactor(domain):    Strategy + Factory limpios

🔴 RED    → test(application):     ObtenerRecomendacionUseCaseImpl con mocks de ports
🟢 GREEN  → feat(application):     implementación del use case
🔵 REFACTOR → refactor(application): orquestación limpia

🔴 RED    → test(infrastructure):  mocks de proveedores + OpenRouteServiceAdapter
🟢 GREEN  → feat(infrastructure):  implementación de adapters y mapper
🔵 REFACTOR → refactor(infrastructure): limpieza

🔴 RED    → test(controller):      POST /api/v1/pedido — escenarios happy path + errores
🟢 GREEN  → feat(controller):      PedidoController + GlobalExceptionHandler
🔵 REFACTOR → refactor(controller): mejoras
```

---

## Convención de Commits

```
test(domain): add failing tests for MotorRecomendacionService R5-R8 🔴
feat(domain): implement MotorRecomendacionService with Strategy pattern 🟢
refactor(domain): apply Factory pattern for strategy selection 🔵

test(application): add failing tests for ObtenerRecomendacionUseCaseImpl 🔴
feat(application): implement ObtenerRecomendacionUseCaseImpl 🟢
refactor(application): clean orchestration 🔵

test(infrastructure): add failing tests for provider mocks and ORS adapter 🔴
feat(infrastructure): implement provider mocks and OpenRouteServiceAdapter 🟢
refactor(infrastructure): improve adapter and mapper 🔵

test(controller): add failing tests for POST /api/v1/pedido 🔴
feat(controller): implement PedidoController 🟢
refactor(controller): improve controller 🔵
```
