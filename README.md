# Optimizador de Envios Backend

Backend en Spring Boot para calcular recomendaciones de envio y confirmar el proveedor seleccionado.

## Que hace este proyecto

Tiene dos flujos principales:

1. Calcular recomendacion de envio segun prioridad.
2. Confirmar el proveedor seleccionado y persistir el pedido.

## Requisitos

- Java 21
- PostgreSQL
- Una API Key de OpenRouteService

## Configuracion local

### 1. Crear la base de datos

En PostgreSQL crea una base llamada:

```sql
CREATE DATABASE optimizador_envios;
```

### 2. Crear tu archivo `.env`

En la raiz del proyecto copia `.env.example` a `.env` y completa los valores reales.

Ejemplo:

```properties
ORS_API_KEY=tu_api_key_real
DB_URL=jdbc:postgresql://localhost:5432/optimizador_envios
DB_USERNAME=postgres
DB_PASSWORD=tu_password
JPA_DDL_AUTO=update
JPA_SHOW_SQL=false
```

### 3. Levantar el proyecto

Con Gradle:

```bash
./gradlew bootRun
```

En Windows:

```bash
gradlew.bat bootRun
```

## Variables importantes

- `ORS_API_KEY`: API Key de OpenRouteService para calcular la distancia entre origen y destino.
- `DB_URL`: URL de conexion a PostgreSQL.
- `DB_USERNAME`: usuario de la base de datos.
- `DB_PASSWORD`: password de la base de datos.
- `JPA_DDL_AUTO`: recomendado `update` en local.

## Endpoints

### 1. Obtener recomendacion

**POST** `/api/v1/pedido`

Calcula las opciones disponibles y devuelve una recomendacion principal con alternativas.

#### Request

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
  }
}
```

#### Response

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

### 2. Confirmar proveedor

**POST** `/api/v1/pedido/confirmar`

Revalida la opcion seleccionada, guarda el pedido confirmado y devuelve la confirmacion persistida.

#### Request

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

#### Response

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

## DTOs que espera el backend

### `OrderDto`

```json
{
  "origin": { "name": "string", "lat": 0, "lng": 0 },
  "destination": { "name": "string", "lat": 0, "lng": 0 },
  "weight": 10,
  "weightUnit": "GRAMS | KILOGRAMS | POUNDS",
  "priority": "COST | TIME"
}
```

### `SelectedOptionDto`

```json
{
  "providerName": "string",
  "cost": 0,
  "currency": "COP",
  "estimatedDays": 1
}
```

## Validaciones importantes

- `origin` es obligatorio.
- `destination` es obligatorio.
- `weight` es obligatorio y debe estar entre `0.001` y `70`.
- `weightUnit` es obligatorio.
- `priority` es obligatorio.
- `selectedOption` es obligatoria al confirmar.
- La opcion seleccionada se recalcula y se valida antes de guardar.

## Ejecutar tests

```bash
./gradlew test
```

## Notas rapidas

- En local la app usa PostgreSQL.
- En tests se usa H2 en memoria.
- Si no tienes `ORS_API_KEY`, el arranque puede funcionar, pero la consulta real de distancia no.
