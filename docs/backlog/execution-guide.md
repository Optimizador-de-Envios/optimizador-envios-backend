## Execution Guide

Guia de ejecucion lineal para implementar la arquitectura definida sin abrir nuevas decisiones de diseno durante el desarrollo.

**Objetivo**
- Implementar el flujo completo del MVP v2 con microservicios.
- Resolver primero el bug funcional existente de doble confirmacion.
- Mantener dependencias claras entre user-service, shipment-service y frontend.

## Principios de ejecucion

1. No implementar sobre contratos ambiguos.
2. Resolver primero los problemas funcionales ya existentes en el flujo actual.
3. Congelar el contrato JWT antes de proteger otros servicios.
4. Implementar por capas siguiendo los backlogs definidos.
5. Dejar la topologia local para cuando los contratos principales ya esten estables.

## Orden de implementacion recomendado

### Paso 0 - Congelar documentos

**Objetivo**
Tomar [service-contracts.md](/c:/Users/sanav/Documents/Sofka/Semana%207/docs/architecture/service-contracts.md) y [topology.md](/c:/Users/sanav/Documents/Sofka/Semana%207/docs/architecture/topology.md) como fuente de verdad.

**Que debe quedar cerrado**
1. `confirmationToken` como clave de idempotencia.
2. Primera confirmacion con `201`.
3. Replay del mismo intento con `200` y mismo body.
4. Historial sin `confirmationToken`.
5. JWT con `JWT_SECRET` compartido.

**Criterio de cierre**
No quedan dudas sobre endpoints, auth, replay ni topologia base.

### Paso 1 - Shipment-Service hardening

**Objetivo**
Resolver primero el bug actual de doble confirmacion en el backend existente.

**Backlog de referencia**
[shipment-service-backlog.md](/c:/Users/sanav/Documents/Sofka/Semana%207/optimizador-envios-backend/docs/backlog/shipment-service-backlog.md)

**Que implementar primero**
1. Fase S0 completa.
2. `confirmationToken` en el dominio y caso de uso.
3. Idempotencia real del comando de confirmacion.

**Criterio de cierre**
El mismo intento no genera dos persistencias distintas aunque se repita la confirmacion.

### Paso 2 - User-Service hasta JWT estable

**Objetivo**
Crear el servicio de identidad y congelar el contrato JWT antes de integrar autenticacion en otros flujos.

**Backlog de referencia**
[user-service-backlog.md](/c:/Users/sanav/Documents/Sofka/Semana%207/optimizador-envios-backend/docs/backlog/user-service-backlog.md)

**Que implementar primero**
1. Bootstrap del proyecto.
2. Registro.
3. Login.
4. Emision JWT con `sub`, `email`, `iat` y `exp`.

**Criterio de cierre**
El shipment-service ya puede validar el token emitido sin reinterpretar el contrato.

### Paso 3 - Shipment-Service autenticado

**Objetivo**
Completar el shipment-service con autenticacion local, ownership del pedido e historial por usuario.

**Que implementar despues**
1. Validacion local del JWT.
2. `userId` persistido como owner externo.
3. Endpoint de historial autenticado.
4. Recomendacion y confirmacion protegidas.

**Criterio de cierre**
Un usuario autenticado puede cotizar, confirmar y consultar solo sus propios pedidos.

### Paso 4 - Frontend hardening y autenticacion

**Objetivo**
Llevar el frontend al nuevo flujo autenticado sin reintroducir el bug actual de reconfirmacion.

**Backlog de referencia**
[frontend-backlog.md](/c:/Users/sanav/Documents/Sofka/Semana%207/optimizador-envios-frontend/docs/backlog/frontend-backlog.md)

**Orden interno recomendado**
1. F0 para corregir la reconfirmacion.
2. Domain.
3. Application.
4. Infrastructure.
5. UI.

**Criterio de cierre**
Flujo completo registro -> login -> recomendacion -> confirmacion -> historial, sin doble confirmacion visible.

### Paso 5 - Topologia local

**Objetivo**
Levantar el ecosistema completo con un solo comando local.

**Que implementar**
1. Proxy del frontend.
2. `docker-compose.yml` raiz.
3. Dos bases separadas.
4. Variables de entorno por servicio.
5. Health checks minimos.

**Criterio de cierre**
Frontend, shipment-service, user-service y ambas bases levantan correctamente en la misma red local.

### Paso 6 - Verificacion integrada

**Objetivo**
Validar el flujo extremo a extremo y confirmar que los contratos y la idempotencia funcionan.

**Que verificar**
1. Registro exitoso.
2. Login exitoso.
3. Recomendacion autenticada.
4. Confirmacion idempotente con replay del mismo `confirmationToken`.
5. Historial del usuario autenticado.
6. Logout y limpieza de sesion.

**Criterio de cierre**
El flujo completo funciona sin inconsistencias entre servicios.

## Resumen ejecutivo

1. Primero corrige el comando de confirmacion en shipment-service.
2. Luego crea user-service hasta congelar JWT.
3. Despues integra autenticacion y ownership en shipment-service.
4. Luego adapta el frontend.
5. Al final arma la topologia local y valida end-to-end.