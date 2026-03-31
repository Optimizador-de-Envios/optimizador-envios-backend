# Historias de Usuario - Optimizador de Envíos (MVP v2)

> Nota: Estas historias corresponden a la versión 2 del MVP (crecimiento post-MVP)

## HU-06 | Visualizar Ruta del Envío en el Mapa

### Descripción

**Como** usuario del sistema  
**Quiero** visualizar en un mapa la ruta entre el origen y el destino del envío  
**Para** entender gráficamente el recorrido estimado del pedido.

### Valor de Negocio
- Permite al usuario comprender visualmente la ruta del envío.
- Mejora la experiencia de usuario al mostrar información geográfica clara.
- Facilita la validación de que el origen y destino seleccionados son correctos.
- Aumenta la confianza del usuario en el cálculo del envío.

### Reglas relacionadas
- **Regla 1:** El sistema debe mostrar la ruta únicamente si existen coordenadas válidas de origen y destino.
- **Regla 2:** La ruta debe ser calculada utilizando un servicio externo de enrutamiento (OpenRouteService).
- **Regla 3:** Las coordenadas deben ser transformadas al formato requerido por la librería de mapas (lat, lng).
- **Regla 4:** El mapa debe ajustarse automáticamente para mostrar toda la ruta.
- **Regla 5:** El sistema debe mostrar marcadores para el origen y el destino.

### Definition of Ready (DoR)
- La historia de usuario está redactada de forma clara.
- Se cuenta con latitud y longitud de origen y destino.
- Está definido el uso de un servicio de cálculo de rutas.
- Está definida la librería de visualización de mapas (Leaflet).
- Los criterios de aceptación están definidos.
- La historia está revisada por DEV y QA.
- La historia puede ser estimada por el equipo técnico.

### Criterios de Aceptación

```gherkin
Escenario: Visualización de ruta en el mapa
    Dado que el usuario ha ingresado un origen y un destino válidos
    Y el sistema ha calculado la ruta entre ambos puntos
    Cuando el usuario visualiza el mapa
    Entonces el sistema debe mostrar la ruta en el mapa
    Y debe mostrar un marcador en el origen
    Y debe mostrar un marcador en el destino
```

```gherkin
Escenario: Ajuste automático del mapa
    Dado que el sistema ha dibujado la ruta en el mapa
    Cuando la ruta es visible
    Entonces el mapa debe ajustarse automáticamente para mostrar toda la ruta
```

```gherkin
Escenario: Conversión correcta de coordenadas
    Dado que el sistema recibe coordenadas en formato [lng, lat]
    Cuando se dibuja la ruta en el mapa
    Entonces el sistema debe convertirlas a formato [lat, lng]
    Y la ruta debe visualizarse correctamente
```

```gherkin
Escenario: Intento de visualización sin datos de ruta
    Dado que el usuario no ha ingresado origen o destino
    O el sistema no ha podido calcular la ruta
    Cuando intenta visualizar el mapa
    Entonces el sistema no debe mostrar la ruta
    Y debe informar que no hay datos suficientes para visualizar el recorrido
```

### Definition of Done (DoD)
- El mapa se visualiza correctamente en la aplicación.
- La ruta entre origen y destino se dibuja correctamente.
- Se muestran los marcadores de origen y destino.
- El mapa se ajusta automáticamente a la ruta.
- Se realiza correctamente la conversión de coordenadas.
- Se manejan los casos donde no hay datos suficientes.
- Se cumplen los criterios de aceptación definidos.
- La historia fue validada por QA.

---

## HU-07 | Registrar usuario

### Descripción

**Como** usuario del sistema  
**Quiero** registrarme en la plataforma  
**Para** asociar mis pedidos a una cuenta y gestionar mi información en futuras sesiones.

### Valor de Negocio
- Permite identificar a cada usuario dentro de la plataforma.
- Facilita la asociación de pedidos confirmados con un usuario específico.
- Habilita una base para futuras funcionalidades personalizadas.
- Mejora la trazabilidad de la operación por usuario.

### Reglas relacionadas
- **Regla 12:** El usuario debe ingresar obligatoriamente nombre, correo electrónico y contraseña para registrarse.
- **Regla 13:** El correo electrónico debe ser único por usuario dentro del sistema.
- **Regla 14:** La contraseña debe cumplir una longitud mínima de 8 caracteres.

### Definition of Ready (DoR)
- La historia de usuario está redactada de forma clara.
- Están definidos los datos obligatorios para el registro del usuario.
- Está definida la validación de unicidad del correo electrónico.
- Está definida la regla mínima de seguridad para la contraseña.
- Los criterios de aceptación están definidos.
- La historia está revisada por DEV y QA.
- La historia puede ser estimada por el equipo técnico.

### Criterios de Aceptación

```gherkin
Escenario: Registro exitoso de usuario
    Dado que una persona desea utilizar la plataforma
    Cuando ingresa un nombre, un correo electrónico único y una contraseña válida
    Entonces el sistema debe registrar al usuario correctamente
    Y debe dejar disponible la cuenta para iniciar sesión
```

```gherkin
Escenario: Intento de registro con correo ya existente
    Dado que ya existe un usuario registrado con un correo electrónico
    Cuando otra persona intenta registrarse con ese mismo correo
    Entonces el sistema no debe permitir el registro
    Y debe informar que el correo electrónico ya se encuentra en uso
```

```gherkin
Escenario: Intento de registro con campos obligatorios vacíos
    Dado que una persona desea crear una cuenta
    Cuando intenta registrarse sin completar nombre, correo electrónico o contraseña
    Entonces el sistema no debe permitir el registro
    Y debe informar que todos los campos son obligatorios
```

```gherkin
Escenario: Intento de registro con contraseña inválida
    Dado que una persona desea crear una cuenta
    Cuando ingresa una contraseña con menos de 8 caracteres
    Entonces el sistema no debe permitir el registro
    Y debe informar que la contraseña no cumple las reglas mínimas de seguridad
```

### Definition of Done (DoD)
- La funcionalidad de registro de usuarios está implementada.
- El sistema permite registrar usuarios con datos válidos.
- El sistema bloquea registros con correos duplicados.
- El sistema bloquea registros con campos obligatorios vacíos.
- El sistema valida la longitud mínima de la contraseña.
- Se cumplen los criterios de aceptación definidos.
- La historia fue validada por QA.

---

## HU-08 | Iniciar sesión

### Descripción

**Como** usuario registrado del sistema  
**Quiero** iniciar sesión en la plataforma  
**Para** acceder a mis pedidos y continuar con mi proceso dentro de la aplicación.

### Valor de Negocio
- Permite autenticar de forma segura a los usuarios registrados.
- Habilita el acceso a funcionalidades personalizadas por usuario.
- Protege la información asociada a cada cuenta.
- Prepara la plataforma para la consulta de historial de pedidos.

### Reglas relacionadas
- **Regla 15:** Solo los usuarios previamente registrados pueden iniciar sesión en la plataforma.
- **Regla 16:** El sistema debe validar el correo electrónico y la contraseña antes de conceder acceso.
- **Regla 17:** El sistema no debe permitir el acceso si las credenciales son inválidas.
- **Regla 18:** Solo un usuario autenticado puede acceder a información personalizada de su cuenta.

### Definition of Ready (DoR)
- La historia de usuario está redactada de forma clara.
- Están definidas las credenciales necesarias para el inicio de sesión.
- Está definida la validación de autenticación.
- Está definido el comportamiento ante credenciales inválidas.
- Los criterios de aceptación están definidos.
- La historia está revisada por DEV y QA.
- La historia puede ser estimada por el equipo técnico.

### Criterios de Aceptación

```gherkin
Escenario: Inicio de sesión exitoso
    Dado que el usuario ya se encuentra registrado en la plataforma
    Cuando ingresa un correo electrónico y una contraseña válidos
    Entonces el sistema debe permitir el acceso a su cuenta
    Y debe habilitar las funcionalidades asociadas al usuario autenticado
```

```gherkin
Escenario: Intento de inicio de sesión con credenciales inválidas
    Dado que el usuario intenta acceder a la plataforma
    Cuando ingresa un correo electrónico o una contraseña incorrectos
    Entonces el sistema no debe permitir el acceso
    Y debe informar que las credenciales son inválidas
```

```gherkin
Escenario: Intento de acceso a funcionalidad privada sin autenticación
    Dado que existe una funcionalidad exclusiva para usuarios autenticados
    Cuando una persona intenta acceder sin haber iniciado sesión
    Entonces el sistema no debe permitir el acceso
    Y debe informar que debe autenticarse para continuar
```

### Definition of Done (DoD)
- La funcionalidad de inicio de sesión está implementada.
- El sistema permite autenticarse con credenciales válidas.
- El sistema bloquea el acceso con credenciales inválidas.
- El sistema protege las funcionalidades privadas para usuarios no autenticados.
- Se cumplen los criterios de aceptación definidos.

---

## HU-09 | Consultar pedidos del usuario

### Descripción

**Como** usuario autenticado del sistema  
**Quiero** consultar los pedidos asociados a mi cuenta  
**Para** hacer seguimiento a mis solicitudes y revisar mis decisiones anteriores.

### Valor de Negocio
- Permite al usuario revisar el historial de pedidos realizados.
- Mejora la trazabilidad de la información persistida en el sistema.
- Aporta continuidad al proceso luego de la selección y confirmación del proveedor.
- Incrementa el valor del producto al ofrecer gestión básica de cuenta.

### Reglas relacionadas
- **Regla 19:** El sistema debe asociar cada pedido confirmado al usuario autenticado que realizó la operación.
- **Regla 20:** El usuario autenticado solo puede visualizar los pedidos asociados a su propia cuenta.
- **Regla 21:** El sistema debe mostrar como mínimo origen, destino, peso, prioridad y proveedor seleccionado por cada pedido.
- **Regla 22:** Si el usuario no tiene pedidos registrados, el sistema debe informar que no existen pedidos asociados a su cuenta.

### Definition of Ready (DoR)
- La historia de usuario está redactada de forma clara.
- Está definida la relación entre usuario y pedido persistido.
- Están definidos los datos mínimos que se mostrarán por pedido.
- Está definido el criterio de seguridad para restringir la consulta a la cuenta autenticada.
- Los criterios de aceptación están definidos.
- La historia está revisada por DEV y QA.
- La historia puede ser estimada por el equipo técnico.

### Criterios de Aceptación

```gherkin
Escenario: Visualización de pedidos del usuario autenticado
    Dado que el usuario ha iniciado sesión en la plataforma
    Y existen pedidos asociados a su cuenta
    Cuando consulta su listado de pedidos
    Entonces el sistema debe mostrar únicamente los pedidos del usuario autenticado
    Y cada pedido debe incluir origen, destino, peso, prioridad y proveedor seleccionado
```

```gherkin
Escenario: Usuario autenticado sin pedidos registrados
    Dado que el usuario ha iniciado sesión en la plataforma
    Y no existen pedidos asociados a su cuenta
    Cuando consulta su listado de pedidos
    Entonces el sistema debe informar que no existen pedidos registrados para ese usuario
```

```gherkin
Escenario: Intento de consultar pedidos de otro usuario
    Dado que existen pedidos asociados a diferentes usuarios
    Cuando un usuario autenticado consulta su información
    Entonces el sistema no debe mostrar pedidos de otras cuentas
    Y debe restringir la información a los pedidos asociados al usuario autenticado
```

### Definition of Done (DoD)
- La funcionalidad de consulta de pedidos por usuario está implementada.
- El sistema asocia los pedidos confirmados al usuario autenticado.
- El sistema muestra únicamente los pedidos correspondientes a la cuenta autenticada.
- El sistema informa cuando un usuario no tiene pedidos registrados.
- Se muestran los datos mínimos definidos para cada pedido.
- Se cumplen los criterios de aceptación definidos.
- La historia fue validada por QA.

---

## Estimación de las Historias de Usuario (MVP v2)

| Historia de Usuario | Estimación (Story Points) |
|---------------------|---------------------------:|
| HU-06 | 5 |
| HU-07 | 5 |
| HU-08 | 3 |
| HU-09 | 5 |

> 💡 Nota: Las estimaciones consideran complejidad, esfuerzo y riesgos asociados a cada historia para la versión 2 del MVP.

---

**Notación:** 
- Las estimaciones se realizaron utilizando la técnica de **Story Points**, considerando la complejidad, el esfuerzo y el riesgo asociado a cada historia de usuario.

**Rúbrica de estimación:**
- **1 SP:** Historia sencilla, sin dependencias y sin incertidumbres importantes.
- **2 SP:** Historia con baja complejidad, con pocas dependencias o incertidumbres menores.
- **3 SP:** Historia de complejidad media, con algunas dependencias o incertidumbres moderadas.
- **5 SP:** Historia compleja, con múltiples dependencias o incertidumbres significativas.
- **8 SP:** Historia muy compleja, con muchas dependencias o incertidumbres altas.
- **13 SP:** Historia extremadamente compleja, con numerosas dependencias o incertidumbres muy altas (En algunos casos, puede ser dividida en historias más pequeñas).

---

Autores: **Nahuel Lemes** y **Santiago Angarita**.
