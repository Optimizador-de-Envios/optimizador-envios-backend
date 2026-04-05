## Shipment-Service Backlog

**Target path**
- c:\Users\sanav\Documents\Sofka\Semana 7\optimizador-envios-backend\docs\backlog\shipment-service-backlog.md

**Objetivo**
Convertir el backend actual en shipment-service, manteniendo su dominio de envios y agregando autenticacion federada basada en JWT, asociacion de pedidos confirmados al userId autenticado, historial de pedidos del usuario e idempotencia en confirmacion.

**Orden TDD por capas**
1. Domain
2. Application
3. Infrastructure
4. Controllers

## Fase S1 - Domain
1. RED: probar que una confirmacion de pedido debe pertenecer a un owner identificado por userId.
2. GREEN: extender el modelo de ConfirmacionPedido para representar ownership sin depender de UsuarioEntity.
3. REFACTOR: mantener el dominio puro y desacoplado de auth framework.
4. RED: probar reglas del historial de pedidos del usuario autenticado.
5. GREEN: modelar comportamiento de consulta por owner.
6. REFACTOR: extraer validaciones repetidas en value objects o servicios de dominio si aplica.
7. RED: probar reglas del `confirmationToken` como identificador de intento.
8. GREEN: incorporar `confirmationToken` al dominio correspondiente.
9. REFACTOR: evitar sobrecargar ConfirmacionPedido con responsabilidades del transporte HTTP.

## Fase S2 - Application
1. RED: probar caso de uso para confirmar pedido con userId autenticado.
2. GREEN: adaptar el use case de confirmacion para recibir userId como dependencia de entrada, no desde body.
3. REFACTOR: limpiar la orquestacion del flujo de confirmacion.
4. RED: probar caso de uso de obtener mis pedidos.
5. GREEN: implementar caso de uso de historial por userId.
6. REFACTOR: mantener puertos orientados a dominio, no a DTOs ni a token parsing.
7. RED: probar un servicio o port de validacion de identidad autenticada si se modela en application.
8. GREEN: implementar el contrato minimo para exponer userId autenticado al caso de uso.
9. REFACTOR: asegurar que application no dependa del user-service remoto.
10. RED: probar el caso de uso de obtener recomendacion con generacion de `confirmationToken`.
11. GREEN: adaptar el flujo de recomendacion para devolver ese token.
12. REFACTOR: separar responsabilidad entre calcular cotizacion y preparar intento confirmable.
13. RED: probar idempotencia del caso de uso de confirmacion usando `confirmationToken`.
14. GREEN: consolidar confirmacion idempotente.
15. REFACTOR: dejar contratos de application listos para seguridad y controllers.

## Fase S3 - Infrastructure
1. RED: probar persistencia de ConfirmacionPedidoEntity con userId externo.
2. GREEN: extender entity, mapper y adapter de repositorio.
3. REFACTOR: mantener userId como primitive externo, sin foreign key entre servicios si las bases estaran separadas.
4. RED: probar query de historial filtrada por userId.
5. GREEN: implementar metodo repository para obtener por userId.
6. REFACTOR: revisar indices y nombres de columnas.
7. RED: probar persistencia y consulta por `confirmationToken`.
8. GREEN: agregar soporte de almacenamiento e indice unico para `confirmationToken` segun el modelo elegido.
9. REFACTOR: mantener clara la separacion entre token de intento y UUID de confirmacion.
10. RED: probar validacion local de JWT en filtros o componentes de seguridad.
11. GREEN: implementar filtro o configuracion de seguridad para extraer claims.
12. REFACTOR: aislar detalles del provider JWT fuera del dominio.
13. RED: probar configuracion externalizada de CORS, secret y puertos.
14. GREEN: adaptar properties y config.
15. REFACTOR: eliminar hardcodes actuales.

## Fase S4 - Controllers
1. RED: probar que POST /api/v1/pedido exige autenticacion y devuelve `confirmationToken`.
2. GREEN: proteger endpoint de recomendacion y adaptar response.
3. REFACTOR: minimizar logica de seguridad en controller.
4. RED: probar que POST /api/v1/pedido/confirmar usa el userId del token y `confirmationToken`, sin aceptar spoofing.
5. GREEN: adaptar controller de confirmacion.
6. REFACTOR: homogenizar responses y errores.
7. RED: probar GET /api/v1/pedido/mis-pedidos.
8. GREEN: implementar endpoint de historial.
9. REFACTOR: consolidar manejo de errores 401, 403, 404 y 400.

## Dependencias y orden recomendado
- S0 debe entrar antes de cerrar el contrato final o en paralelo inmediato con contratos.
- S1 bloquea S2.
- S2 bloquea S3.
- S3 bloquea S4.
- La validacion JWT debe estar lista antes de proteger endpoints productivos.

## Verificacion shipment-service
1. Un usuario autenticado puede cotizar y confirmar.
2. Un usuario no autenticado recibe 401.
3. El userId del token queda persistido en el pedido confirmado.
4. El historial devuelve solo pedidos del owner autenticado.
5. El mismo `confirmationToken` nunca crea dos confirmaciones distintas.
