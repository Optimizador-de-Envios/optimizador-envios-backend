## Topology

Documento de topologia operativa recomendada para integrar frontend, shipment-service y user-service con dos bases de datos separadas y un unico origen HTTP para el navegador.

**Objetivo**
- Levantar todo el ecosistema local con un solo comando.
- Mantener separacion clara entre servicios y sus datos.
- Evitar complejidad prematura como API gateway dedicado o service discovery.
- Facilitar CI local y pruebas integradas.

## Vista general

### Componentes
1. `frontend`
   - Sirve la SPA.
   - Actua como borde HTTP mediante proxy reverse.
   - Expone un unico origen al navegador.
2. `shipment-service`
   - Servicio de envios.
   - Expone recomendacion, confirmacion e historial.
   - Valida JWT localmente.
3. `user-service`
   - Servicio de identidad.
   - Expone registro y login.
   - Emite JWT.
4. `postgres-shipment`
   - Base exclusiva de shipment-service.
5. `postgres-users`
   - Base exclusiva de user-service.

## Flujo de red

1. El navegador llama al `frontend`.
2. El `frontend` sirve la SPA.
3. El `frontend` proxya `/api/users/*` hacia `user-service`.
4. El `frontend` proxya `/api/v1/pedido/*` hacia `shipment-service`.
5. El `shipment-service` valida el JWT de forma local.
6. El `shipment-service` no hace roundtrip al `user-service` por cada request autenticado.

## Topologia logica

```text
Browser
  |
  v
Frontend + Reverse Proxy
  |------------------------------> /api/users/* -----------> User-Service -----------> Postgres Users
  |
  |------------------------------> /api/v1/pedido/* -------> Shipment-Service -------> Postgres Shipment
```

## Topologia local con Compose

### Archivo recomendado
- `c:\Users\sanav\Documents\Sofka\Semana 7\docker-compose.yml`

### Servicios esperados
1. `frontend`
   - Build desde el repo frontend.
   - Puerto publico recomendado: `3000:80`.
   - Debe incluir configuracion nginx con proxy a servicios internos.
2. `shipment-service`
   - Build desde el repo backend actual.
   - Puerto interno recomendado: `8080`.
   - Variables para datasource, JWT, CORS y health.
3. `user-service`
   - Build desde el nuevo repo cuando exista.
   - Puerto interno recomendado: `8081`.
   - Variables para datasource, JWT y health.
4. `postgres-shipment`
   - Puerto expuesto opcional solo para debugging local.
   - Base dedicada a envios.
5. `postgres-users`
   - Puerto expuesto opcional solo para debugging local.
   - Base dedicada a usuarios.

### Red
- Una red docker comun para todos los contenedores.
- Los nombres de servicio del compose resuelven el discovery local.

## Configuracion minima por servicio

### Frontend
- Debe consumir rutas relativas, no hosts hardcodeados en el bundle.
- Recomendacion: dejar `/api/users/*` y `/api/v1/pedido/*` como rutas base.
- El proxy nginx decide el destino real por entorno.

### Shipment-Service
- `SERVER_PORT`
- `DB_URL` o equivalente
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS`
- `JPA_DDL_AUTO` o estrategia de migracion

### User-Service
- `SERVER_PORT`
- `DB_URL` o equivalente
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_SECONDS`
- `CORS_ALLOWED_ORIGINS`
- estrategia de migracion propia

### Bases de datos
- `postgres-shipment` solo para shipment-service.
- `postgres-users` solo para user-service.
- Sin tablas compartidas.
- Sin foreign keys entre servicios.

## Proxy recomendado en frontend

### Reglas
- `/api/users/` -> `http://user-service:8081/api/users/`
- `/api/v1/pedido/` -> `http://shipment-service:8080/api/v1/pedido/`

### Motivo
- Unifica origen HTTP para el navegador.
- Reduce problemas de CORS.
- Evita compilar el frontend con hosts distintos por entorno.
- Permite introducir un gateway real despues sin romper la SPA.

## Persistencia y ownership

### Shipment-Service
- Persiste `userId` como referencia externa del owner del pedido.
- Persiste `confirmationToken` como clave de idempotencia del intento.
- No mantiene foreign key a una tabla del user-service.

### User-Service
- Mantiene solo datos de identidad y autenticacion.
- No guarda pedidos ni historial operativo.

## Health y operabilidad minima

### Recomendado
- Endpoint de health para ambos servicios backend.
- `depends_on` con healthcheck en compose si lo habilitan.
- Logs a stdout para todos los contenedores.
- Variables de entorno centralizadas mediante `.env` del compose.

## Secuencia de despliegue local

1. Levantar `postgres-shipment` y `postgres-users`.
2. Levantar `user-service`.
3. Levantar `shipment-service`.
4. Levantar `frontend`.
5. Verificar registro, login, recomendacion, confirmacion e historial.

## Decisiones topologicas
- Sin API Gateway dedicado por ahora.
- Sin service discovery dedicado por ahora.
- Sin event bus por ahora.
- Sin base de datos compartida.
- Sin sincronizacion remota por request entre shipment-service y user-service.
- El JWT es el mecanismo de propagacion de identidad.
- En esta iteracion el JWT usa `JWT_SECRET` compartido entre ambos servicios.

## Riesgos a vigilar
1. Hardcodes de CORS y hosts en configuraciones actuales.
2. Uso de `ddl-auto=update` como estrategia temporal en lugar de migraciones.
3. Persistencia duplicada si no se implementa la idempotencia del `confirmationToken`.
4. Acoplamiento accidental si shipment-service intenta consultar user-service para resolver cada request autenticado.

## Orden de trabajo recomendado
1. Congelar contrato JWT y contratos HTTP.
2. Crear user-service.
3. Adaptar shipment-service para JWT + `userId` + `confirmationToken`.
4. Configurar proxy del frontend.
5. Crear compose raiz.
6. Ejecutar pruebas integradas end-to-end.
