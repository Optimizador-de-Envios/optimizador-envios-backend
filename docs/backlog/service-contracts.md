## Service Contracts

Documento de contratos minimos para integrar frontend, shipment-service y user-service sin escalar complejidad innecesaria en esta iteracion.

**Objetivo**
- Cubrir el flujo completo del MVP v2: registro, login, cotizacion, confirmacion e historial.
- Mantener un contrato estable para frontend.
- Evitar acoplamiento innecesario entre servicios.
- Incorporar idempotencia en confirmacion para corregir la doble persistencia del mismo intento.

**Principios**
- El frontend consume un unico origen HTTP.
- El user-service es responsable de registro, login y emision de JWT.
- El shipment-service es responsable de cotizacion, confirmacion e historial.
- El frontend nunca envia userId para asociar pedidos.
- El shipment-service obtiene el userId desde el JWT validado localmente.
- El shipment-service no consulta al user-service en cada request autenticado.
- La confirmacion de pedido debe ser idempotente por intento.
- Se evita sobre-diseno: sin refresh token, sin roles, sin endpoints administrativos en esta iteracion.

## User-Service

### POST /api/users/register

**Descripcion**
Registra un nuevo usuario en el sistema.

**Request**
```json
{
  "name": "Juan Perez",
  "email": "juan@example.com",
  "password": "SecurePass123"
}
```

**Response 201**
```json
{
  "id": "c6f5dd0d-55d7-4e52-a1cf-7cf7c26f4d82",
  "name": "Juan Perez",
  "email": "juan@example.com",
  "createdAt": "2026-04-03T18:30:00Z"
}
```

**Errores**
- `400` payload invalido o campos faltantes.
- `409` email ya registrado.

### POST /api/users/login

**Descripcion**
Autentica al usuario y emite un JWT.

**Request**
```json
{
  "email": "juan@example.com",
  "password": "SecurePass123"
}
```

**Response 200**
```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": "c6f5dd0d-55d7-4e52-a1cf-7cf7c26f4d82",
    "name": "Juan Perez",
    "email": "juan@example.com"
  }
}
```

**Errores**
- `400` payload invalido.
- `401` credenciales invalidas.

## Shipment-Service

### POST /api/v1/pedido

**Descripcion**
Calcula la recomendacion de proveedores para el pedido actual y genera un `confirmationToken` asociado a ese intento.

**Header requerido**
- `Authorization: Bearer <jwt>`

**Request**
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

**Response 200**
```json
{
  "confirmationToken": "4c3f9d8c-2d8b-4d4c-b86c-5db313f3d4a3",
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

**Errores**
- `400` payload invalido.
- `401` token ausente o invalido.

### POST /api/v1/pedido/confirmar

**Descripcion**
Confirma el intento actual del pedido usando `confirmationToken`. Debe comportarse de forma idempotente: si el mismo token ya fue confirmado, devuelve la misma confirmacion ya creada.

**Header requerido**
- `Authorization: Bearer <jwt>`

**Request**
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

**Response 201 - Primera confirmacion exitosa**
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

**Response 200 - Replay idempotente del mismo intento**

Devuelve el mismo body de confirmacion ya persistida cuando el mismo usuario confirma nuevamente el mismo `confirmationToken`.

**Comportamiento idempotente esperado**
- Mismo `confirmationToken` confirmado dos veces por el mismo usuario: devolver la misma confirmacion ya persistida.
- `confirmationToken` inexistente o no perteneciente al usuario autenticado: rechazar con error.

**Errores**
- `400` payload invalido o seleccion inconsistente.
- `401` token ausente o invalido.
- `404` confirmationToken no encontrado o no disponible.
- `409` intento de confirmar un token ya invalidado por una regla de negocio futura, si aplica.

### GET /api/v1/pedido/mis-pedidos

**Descripcion**
Devuelve el historial de pedidos confirmados del usuario autenticado.

**Header requerido**
- `Authorization: Bearer <jwt>`

**Response 200**
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

**Errores**
- `401` token ausente o invalido.

## Contrato JWT Compartido

**Issuer**
- `user-service`

**Claims minimos**
- `sub`: userId
- `email`: email del usuario
- `iat`: issued at
- `exp`: expiration

**Reglas**
- Sin roles por ahora.
- Expiracion simple de MVP, por ejemplo 24 horas.
- El shipment-service valida firma y expiracion localmente.
- Estrategia cerrada para esta iteracion: `JWT_SECRET` compartido entre user-service y shipment-service.

## Formato de error comun

**Estructura recomendada**
```json
{
  "message": "La solicitud contiene datos invalidos",
  "errors": [
    "email: debe ser un correo valido"
  ]
}
```

**Codigos base**
- `400` errores de validacion o formato.
- `401` autenticacion ausente o token invalido.
- `404` recurso o token de confirmacion no encontrado.
- `409` conflictos de negocio, por ejemplo email duplicado.

## Decisiones explicitas
- El historial se mantiene en shipment-service.
- No existe endpoint para consultar pedidos de otro usuario.
- `confirmationToken` resuelve el bug de doble confirmacion y formaliza la idempotencia del comando.
- El frontend debe tratar `confirmationToken` como parte del estado del intento actual.
- En esta iteracion el `confirmationToken` no expira por tiempo.
