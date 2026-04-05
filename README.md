# Optimizador de Envio Backend

Servicio backend en Spring Boot para calcular recomendaciones de envio, confirmar un proveedor y consultar el historial de pedidos del usuario autenticado.

## Que hace

- Calcula la mejor opcion de envio segun prioridad de costo o tiempo.
- Permite confirmar un pedido con un proveedor seleccionado.
- Devuelve el historial de pedidos del usuario autenticado.
- Usa proveedores simulados para FedEx, DHL y un proveedor local.

## Requisitos

- Java 21
- PostgreSQL
- Una API key de OpenRouteService
- Un JWT valido emitido por el user-service para los endpoints protegidos

## Configuracion inicial

1. Copia el archivo [.env.example](.env.example) a `.env` en la raiz del proyecto.
2. Completa los valores de base de datos, JWT y OpenRouteService.
3. Verifica que PostgreSQL este levantado y que la base exista.

Valores esperados en `.env`:

```properties
JWT_SECRET=your_shared_jwt_secret
DB_URL=jdbc:postgresql://localhost:5432/optimizador_envios
DB_USERNAME=postgres
DB_PASSWORD=postgres
JPA_DDL_AUTO=update
JPA_SHOW_SQL=false
ORS_API_KEY=YOUR_API_KEY_HERE
```

## Como ejecutar

Con Gradle en Linux/Mac:

```bash
./gradlew bootRun
```

En Windows:

```powershell
gradlew.bat bootRun
```

## Endpoints

Base URL: `http://localhost:8080/api/v1/pedido`

### POST `/api/v1/pedido`

Calcula la recomendacion de envio.

Protegido: no.

Request:

```json
{
  "order": {
    "origin": {
      "name": "Tunja, BY, Colombia",
      "lat": 5.53528,
      "lng": -73.36778
    },
    "destination": {
      "name": "Bogota, DC, Colombia",
      "lat": 4.635456,
      "lng": -74.08768
    },
    "weight": 10,
    "weightUnit": "KILOGRAMS",
    "priority": "COST"
  }
}
```

Response 200:

```json
{
  "recommendation": {
    "providerName": "Local",
    "cost": 30386.59,
    "currency": "COP",
    "estimatedDays": 1
  },
  "alternatives": [
    {
      "providerName": "DHL",
      "cost": 35500.0,
      "currency": "COP",
      "estimatedDays": 1
    }
  ]
}
```

### POST `/api/v1/pedido/confirmar`

Confirma el pedido y lo persiste.

Protegido: si.

Header requerido:

```http
Authorization: Bearer <jwt>
```

Request:

```json
{
  "confirmationToken": "4c3f9d8c-2d8b-4d4c-b86c-5db313f3d4a3",
  "order": {
    "origin": {
      "name": "Tunja, BY, Colombia",
      "lat": 5.53528,
      "lng": -73.36778
    },
    "destination": {
      "name": "Bogota, DC, Colombia",
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

Response 201:

```json
{
  "id": "a2fbf767-f7e3-4f9d-a8d4-7051119816c2",
  "confirmationToken": "4c3f9d8c-2d8b-4d4c-b86c-5db313f3d4a3",
  "origin": {
    "name": "Tunja, BY, Colombia",
    "lat": 5.53528,
    "lng": -73.36778
  },
  "destination": {
    "name": "Bogota, DC, Colombia",
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
  },
  "createdAt": "2026-04-03T18:35:00Z"
}
```

### GET `/api/v1/pedido/mis-pedidos`

Devuelve el historial del usuario autenticado.

Protegido: si.

Header requerido:

```http
Authorization: Bearer <jwt>
```

Response 200:

```json
[
  {
    "id": "a2fbf767-f7e3-4f9d-a8d4-7051119816c2",
    "origin": {
      "name": "Tunja, BY, Colombia",
      "lat": 5.53528,
      "lng": -73.36778
    },
    "destination": {
      "name": "Bogota, DC, Colombia",
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
    },
    "createdAt": "2026-04-03T18:35:00Z"
  }
]
```

## Como probarlo

- `./gradlew test` para ejecutar los tests.
- Probar con Postman o Thunder Client.
- Si usas el frontend local, la app espera origen `http://localhost:5173`.
- Los endpoints protegidos son `POST /api/v1/pedido/confirmar` y `GET /api/v1/pedido/mis-pedidos`.
- En esos endpoints debes enviar un JWT valido en `Authorization: Bearer <jwt>`.

## Notas utiles

- Si no configuras `ORS_API_KEY`, el calculo real de distancia puede fallar.
- Los datos de proveedores estan simulados en el backend.
- El JWT compartido debe coincidir con el user-service.