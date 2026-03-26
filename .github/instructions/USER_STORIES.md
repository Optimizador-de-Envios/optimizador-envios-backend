# Historias de Usuario - Optimizador de Envíos

## HU-01 | Registrar Pedido de Envío

### Descripción

**Como** usuario del sistema  
**Quiero** registrar un pedido  
**Para** poder hacer el proceso del cálculo del envío.

### Valor de Negocio
- Permite registrar un pedido con los datos necesarios para calcular el envío.
- Asegura que el sistema tenga la información correcta para generar recomendaciones de envío.
- Facilita la validación de datos de entrada para evitar errores en el proceso de cálculo.

### Reglas relacionadas
- **Regla 1:** El sistema solo debe operar para envíos cuyo origen y destino estén dentro de Colombia.
- **Regla 2:** El usuario debe ingresar obligatoriamente origen, destino y peso del paquete.
- **Regla 3:** El sistema no debe permitir calcular opciones si el paquete no esta en los límites de peso (0,001 Kg - 70 Kg)

### Definition of Ready (DoR)

- La historia de usuario está redactada de forma clara.
- Están definidos los datos de usuario para registrar el pedido: origen, peso y destino
- Las reglas de negocio sobre cobertura y peso permitido están claras.
- Los criterios de aceptación están definidos.
- La historia está revisada por DEV y QA.
- La historia puede ser estimada por el equipo técnico.

### Criterios de Aceptación

```gherkin
Escenario: Ingreso de Datos Válidos
    Dado que el usuario necesita enviar un producto
    Cuando ingresa un origen, un destino y un peso válidos
    Entonces el sistema debe registrar los datos del pedido
    Y debe permitir continuar con el cálculo del envío
```

```gherkin
Escenario: Intento de registro con campos vacíos
    Dado que el usuario necesita enviar un producto
    Cuando intenta registrar un pedido sin ingresar origen, destino o peso
    Entonces el sistema no debe permitir continuar con el cálculo del envío
    Y debe informar que todos los campos son obligatorios
```

```gherkin
Escenario: Destino sin Cobertura
    Dado que el usuario necesita enviar un producto
    Cuando ingresa un destino fuera de la cobertura de los proveedores logísticos
    Entonces el sistema no debe permitir continuar con el cálculo del envío
    Y debe informar que no hay proveedores disponibles para ese destino
```

```gherkin
Escenario: Peso fuera del máximo permitido
    Dado que el usuario necesita enviar un producto
    Cuando ingresa un peso mayor al permitido por los proveedores logísticos
    Entonces el sistema no debe permitir continuar con el cálculo del envío
    Y debe informar que el peso ingresado no está cubierto por los proveedores disponibles
```

```gherkin
Escenario: Peso fuera del mínimo permitido
    Dado que el usuario necesita enviar un producto
    Cuando ingresa un peso menor al permitido por los proveedores logísticos
    Entonces el sistema no debe permitir continuar con el cálculo del envío
    Y debe informar que el peso ingresado no está cubierto por los proveedores disponibles
```

### Definition of Done (DoD)
- La funcionalidad de registro de pedido está implementada.
- El sistema permite registrar origen, destino y peso válidos.
- El sistema bloquea destinos sin cobertura.
- El sistema bloquea pesos no permitidos.
- Se cumplen los criterios de aceptación definidos.
- La historia fue validada por QA.

---

## HU-02 | Definir prioridad del envío

### Descripcion

**Como** usuario del sistema  
**Quiero** seleccionar si prefiero un envío económico o más rápido  
**Para** que el sistema realice una recomendación según mi preferencia.

### Valor de Negocio
- Permite seleccionar la opción de envío que es de interés para el usuario de acuerdo a sus necesidades
- Asegura que el sistema tenga la información correcta para generar recomendaciones de envío.

### Reglas Relacionadas
- **Regla 4:** El usuario debe seleccionar una prioridad de envío (menor costo o menor tiempo) para que el sistema pueda generar la recomendación.

### Definition of Ready (DoR)

- La historia de usuario está redactada de forma clara.
- Están definidas las opciones de envío que se tendran en cuenta; económico o rápido
- Los criterios de aceptación están definidos.
- La historia está revisada por DEV y QA.
- La historia puede ser estimada por el equipo técnico.

### Criterios de Aceptación

```gherkin
Escenario: Selección de prioridad por menor costo
    Dado que el usuario registró un pedido válido
    Cuando selecciona prioridad de menor costo
    Entonces el sistema debe utilizar el costo como criterio principal para generar la recomendación
```

```gherkin
Escenario: Selección de prioridad por menor tiempo de entrega
    Dado que el usuario registró un pedido válido
    Cuando selecciona prioridad de menor tiempo de entrega
    Entonces el sistema debe utilizar el tiempo de entrega como criterio para generar la recomendación
```

```gherkin
Escenario: No selección de prioridad
    Dado que el usuario registró un pedido válido
    Cuando no selecciona ninguna prioridad de envío
    Entonces el sistema no debe permitir continuar con el cálculo del envío
    Y debe informar que se debe seleccionar una prioridad de envío
```

### Definition of Done (DoD)
- La funcionalidad de elegir la prioridad del envío está implementada.
- El sistema permite elegir entre las opciones de envío económico o rápido.
- El sistema no permite hacer el cálculo para la recomendación sin antes haber elegido una de las opciones de envío.
- Se cumplen los criterios de aceptación definidos.
- La historia fue validada por QA.

---

## HU-03 | Obtener recomendación principal de proveedor de envío

### Descripción

**Como** usuario del sistema  
**Quiero** que el sistema me recomiende la mejor opción de envío  
**Para** elegir una alternativa que cumpla con mis necesidades

### Valor de Negocio
- Proporciona una recomendación personalizada basada en las preferencias del usuario.
- Facilita la toma de decisiones al destacar la opción más relevante.
- Mejora la experiencia del usuario al ofrecer una recomendación clara y directa.

### Reglas relacionadas
- **Regla 5:** Si la prioridad es menor costo, el sistema debe recomendar la opción de menor costo disponible.

- **Regla 6:** Si la prioridad es menor tiempo de entrega, el sistema debe recomendar la opción de menor tiempo disponible.

- **Regla 7:** Si existe empate en el menor costo, el sistema debe recomendar la opción con menor tiempo de entrega entre las empatadas.

- **Regla 8:** Si existe empate en el menor tiempo de entrega, el sistema debe recomendar la opción con menor costo entre las empatadas.

### Definition of Ready (DoR)
- La historia de usuario está redactada de forma clara.
- Estan definidas las prioridades de envío disponibles: menor costo y menor tiempo de entrega.
- Están definidas las reglas de negocio para generar la recomendación principal.
- Los criterios de aceptación están definidos.
- La historia está revisada por DEV y QA.
- La historia puede ser estimada por el equipo técnico.

### Criterios de Aceptación

```gherkin
Escenario: Recomendación principal según prioridad de menor costo
    Dado que el usuario definió la prioridad de menor costo
    Cuando el sistema calcula las opciones disponibles
    Entonces el sistema debe devolver la opción recomendada
    Y la opción recomendada debe corresponder a la opción de menor costo disponible
```

```gherkin
Escenario: Recomendación principal según prioridad de menor tiempo
    Dado que el usuario definió la prioridad de menor tiempo
    Cuando el sistema calcula las opciones disponibles
    Entonces el sistema debe devolver la opción recomendada
    Y la opción recomendada debe corresponder a la opción de menor tiempo disponible
```

```gherkin
Escenario: Recomendación principal con empate en menor costo
    Dado que el usuario definió la prioridad de menor costo
    Y existen múltiples opciones con el mismo menor costo disponible
    Cuando el sistema calcula las opciones disponibles
    Entonces el sistema debe devolver una opción recomendada
    Y la opción recomendada debe corresponder a la de menor tiempo de entrega
```

```gherkin
Escenario: Recomendación principal con empate en menor tiempo
    Dado que el usuario definió la prioridad de menor tiempo
    Y existen múltiples opciones con el mismo menor tiempo disponible
    Cuando el sistema calcula las opciones disponibles
    Entonces el sistema debe devolver una opción recomendada
    Y la opción recomendada debe corresponder a la de menor costo
```

### Definition of Done (DoD)
- La funcionalidad de generar una recomendación principal está implementada.
- El sistema devuelve la opción recomendada de acuerdo a la prioridad seleccionada por el usuario.
- El sistema maneja correctamente los casos de empate en costo o tiempo.
- Se cumplen los criterios de aceptación definidos.
- La historia fue validada por QA.

---

## HU-04 | Obtener opciones alternativas de proveedores

### Descripción

**Como** usuario del sistema  
**Quiero** ver otras opciones disponibles  
**Para** compararlas con la recomendación principal y evaluar la opción más conveniente.

### Valor de negocio
- Permite al usuario tener control todo el tiempo sobre la decisión de que proveedor usar.
- La herramienta no solo se limita a recomendar una opción, sino que también ofrece transparencia al mostrar otras alternativas disponibles.
- Permite al usuario tener claridad sobre otras opciones de proveedores de acuerdo a su pedido 

### Reglas relacionadas

- **Regla 9:** El sistema debe mostrar opciones alternativas distintas a la recomendación principal, incluyendo costo y tiempo de entrega, para permitir la comparación.

### Definition of Ready (DoR)
- La historia de usuario está redactada de forma clara.
- Están definidas las reglas de negocio para generar las opciones de proveedores alternativas teniendo en cuenta las opciones de económico o rápido.
- Están definidas las reglas de negocio relacionadas con los proveedores (peso y cobertura).
- Los criterios de aceptación están definidos.
- La historia está revisada por DEV y QA.
- La historia puede ser estimada por el equipo técnico.

### Criterios de aceptación

```gherkin
Escenario: Visualización de alternativas
    Dado que el sistema generó una recomendación principal
    Y existen otras opciones de envío disponibles 
    Cuando el sistema muestra la recomendación principal
    Entonces el sistema debe mostrar automáticamente las opciones alternativas disponibles
```

```gherkin
Escenario: Ausencia de opciones alternativas
    Dado que el sistema generó una recomendación principal
    Y no existen otras opciones de envío disponibles
    Cuando el sistema muestra la recomendación principal
    Entonces el sistema debe notificar que no existen otras opciones alternativas
```

### Definition of Done (DoD)
- La funcionalidad de generar las opciones alternativas de proveedores está implementada.
- El sistema devuelve las opciones alternativas si el caso se cumple para generarlas.
- Se cumplen los criterios de aceptación definidos.
- La historia fue validada por QA.

---

## HU-05 | Seleccionar y confirmar proveedor

### Descripción

**Como** usuario del sistema  
**Quiero** seleccionar el proveedor deseado  
**Para** continuar con el proceso del pedido

### Valor de Negocio
- Facilita la toma de decisiones al ofrecer una selección clara entre las opciones disponibles.
- Mejora la experiencia del usuario al permitirle confirmar su elección de proveedor.

### Reglas relacionadas

- **Regla 10:** El usuario debe seleccionar un proveedor para poder continuar con el proceso del pedido.
- **Regla 11:** El sistema debe persistir la información del pedido cuando el usuario seleccione y confirme un proveedor para continuar con el proceso.

### Definition of Ready (DoR)
- La historia de usuario está redactada de forma clara.
- Están definidas las reglas de negocio para la selección y confirmación del proveedor.
- Está definida la regla de negocio para continuar o bloquear el proceso de selección de proveedor.
- Está definida la regla de negocio para persistir la información del pedido al seleccionar un proveedor.
- Los criterios de aceptación están definidos.
- La historia está revisada por DEV y QA.
- La historia puede ser estimada por el equipo técnico.

### Criterios de aceptación

```gherkin
Escenario: Camino Feliz - Selección del proveedor deseado
	Dado que el sistema mostró la opción recomendada y las opciones disponibles
	Cuando el usuario seleccione el proveedor deseado
	Entonces el sistema debe continuar con el proceso de pedido
```

```gherkin
Escenario: Usuario no selecciona proveedor
	Dado que el sistema mostró la opción recomendada y las opciones disponibles
	Cuando el usuario intenta continuar sin seleccionar un proveedor
	Entonces el sistema no debe continuar con el proceso de pedido
	Y debe informar que se debe seleccionar un proveedor para continuar 
```

```gherkin
Escenario: Persistencia de información al seleccionar proveedor
    Dado que el sistema mostró la opción recomendada y las opciones disponibles
    Cuando el usuario seleccione un proveedor y confirme su selección
    Entonces el sistema debe persistir la información del pedido con el proveedor seleccionado
    Y debe permitir continuar con el proceso del pedido
```

### Definition of Done (DoD)
- La funcionalidad de selección y confirmación del proveedor está implementada.
- El sistema permite seleccionar un proveedor de las opciones disponibles.
- El sistema bloquea el proceso de pedido si no se ha seleccionado un proveedor.
- El sistema persiste la información del pedido al seleccionar y confirmar un proveedor.
- Se cumplen los criterios de aceptación definidos.
- La historia fue validada por QA.

---

## Estimación de las Historias de Usuario

| Historia de Usuario | Estimación (Story Points) |
|---------------------|--------------------------------|
| HU-01 | 5 |
| HU-02 | 3 |
| HU-03 | 8 |
| HU-04 | 3 |
| HU-05 | 5 |

> 💡 Nota: 
> La justificación de las estimaciones se encuentra detallada en el documento de subtareas (SUBTASKS.md) para cada historia de usuario.

---

**Notación:** 
- La estimación se realizó utilizando la técnica de **Story Points**, considerando la complejidad, el esfuerzo y el riesgo asociado a cada historia de usuario.

**Rúbrica de estimación:**
- **1 SP:** Historia sencilla, sin dependencias y ni incertidumbres importantes.
- **2 SP:** Historia con baja complejidad, con pocas dependencias o incertidumbres menores.
- **3 SP:** Historia de complejidad media, con algunas dependencias o incertidumbres moderadas.
- **5 SP:** Historia compleja, con múltiples dependencias o incertidumbres significativas.
- **8 SP:** Historia muy compleja, con muchas dependencias o incertidumbres altas.
- **13 SP:** Historia extremadamente compleja, con numerosas dependencias o incertidumbres muy altas (En algunos casos, puede ser dividida en historias más pequeñas).

---

Autores: **Nahuel Lemes** y **Santiago Angarita**.
