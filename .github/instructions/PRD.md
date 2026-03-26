# Producto: Optimizador de envíos

## 1. Introducción:

### 1.1 Problema
A la hora de realizar el envío de un producto, las empresas/personas pueden encontrarse con el problema de no saber qué proveedor logístico elegir.
Esto puede resultar problemático ya que una selección inadecuada puede generar mayores costos de envío o tiempos de entrega que no cumplan con lo esperado por el cliente final.

### 1.2 Propuesta de valor
El Optimizador de Envíos propone una solución que resuelve este conflicto de forma rápida, automatizada y eficiente, brindando al cliente la posibilidad de elegir si desea priorizar menor costo o mayor velocidad.

---

## 2. Visión y Objetivos

### 2.1 Visión
Construir una herramienta logística que automatice la selección del proveedor de envíos más conveniente, ayudando a mejorar las condiciones de precio y tiempo de entrega para el cliente final.

### 2.2 Objetivos

| Objetivo | Descripción |
| --- | --- |
| **O1** | Automatizar la selección del proveedor de transporte evaluando las opciones entre FedEx, DHL y proveedores locales. |
| **O2** | Centralizar múltiples proveedores de transporte en una sola herramienta simplificando el proceso de toma de decisiones. |
| **O3** | Permitir la optimización del envío según la prioridad elegida por el cliente: menor costo o  menor tiempo de entrega. |
| **O4** | Unificar y proveer estimaciones de precio y tiempo de entrega para entregar al cliente la opción más adecuada a sus requerimientos. |

---

## 3. Reglas de Negocio Generales

- **Regla 1:** El sistema **solo debe operar** para envíos dentro de **Colombia**.
- **Regla 2:** Para calcular una cotización, el usuario debe ingresar **obligatoriamente origen, destino y peso del paquete**.
- **Regla 3:** El sistema no debe permitir calcular opciones si el paquete supera los límites admitidos por los proveedores disponibles; **el peso debe ser mayor o igual a 0,001 Kg y menor o igual a 70 Kg**.
- **Regla 4:** El usuario debe seleccionar una prioridad de envío (menor costo o menor tiempo) para que el sistema pueda generar la recomendación.
- **Regla 5:** Si la **prioridad es menor costo**, el **sistema** debe **recomendar la opción de menor costo** disponible.
- **Regla 6:** Si la **prioridad es menor tiempo de entrega**, el sistema **debe recomendar la opción de menor tiempo** disponible.
- **Regla 7:** Si **existe empate en el menor costo**, el sistema debe **recomendar la opción con menor tiempo de entrega** entre las empatadas.
- **Regla 8:** Si **existe empate en el menor tiempo de entrega**, el sistema debe **recomendar la opción con menor costo** entre las empatadas.
- **Regla 9:** El sistema debe **mostrar opciones alternativas** distintas a la recomendación principal para permitir la comparación si se da el caso.
- **Regla 10:** El usuario debe **seleccionar un proveedor** para poder continuar con el proceso del pedido.
- **Regla 11:** El sistema debe **persistir la información del pedido** cuando el usuario seleccione y confirme un proveedor para continuar con el proceso.

---

## 4. Alcance del MVP

### 4.1 In Scope
- **Registro de datos del envío:** el sistema permite ingresar origen, destino y peso del pedido.
- **Selector de prioridad del envío**: el sistema permite elegir si se desea priorizar **menor costo** o **menor tiempo de entrega**.
- **Motor de evaluación centralizado:** el sistema compara las opciones de transporte disponibles (*FedEx*, *DHL* y *proveedores locales*) y evalúa considerando costo y tiempo estimado.
- **Generación de recomendación principal:** el sistema devuelve la opción más adecuada según la prioridad seleccionada por el usuario.
- **Visualización de opciones alternativas:** el sistema muestra otras opciones disponibles para que el usuario pueda compararlas con la recomendación principal.
- **Selección de proveedor:** el usuario puede elegir una de las opciones disponibles para continuar con el proceso del pedido.

### 4.2 Out of Scope
- **Gestión de usuarios y perfiles.**
- **Integración en tiempo real con APIs de proveedores logísticos.**
- **Módulo de pagos.**
- **Seguimiento del envío (tracking).**
- **Gestión de pedidos internacionales.**

> 💡 **Nota sobre proveedores:** En el MVP, los proveedores (FedEx, DHL y un proveedor local representativo) 
> se modelarán con datos simulados (mock). El proveedor local representa una empresa de mensajería 
> regional colombiana con tarifas y tiempos predefinidos en el sistema.

---

## 5. Riesgos técnicos y de negocio

> **Notación:**  
> El impacto y la probabilidad de cada riesgo se clasifican de 1 a 3, donde 1 es bajo, 2 es medio y 3 es alto.  
> El riesgo se calcula como el producto del impacto y la probabilidad, y se clasifica como bajo (1-3), medio (4-6) o alto (7-9).

### 5.1 Riesgos de Negocio

| Riesgo | Descripción | Impacto | Probabilidad | Riesgo | Mitigación |
| --- | --- | --- | --- | --- | --- |
| **R1: Pérdidas financieras por fallos de lógica** | Si el algoritmo calcula mal el cruce entre peso y distancia desde el origen al destino, podría asignar sistemáticamente la opción más cara cuando el usuario prioriza el costo. | 3 | 2 | Medio (6) | Realizar pruebas exhaustivas del algoritmo de recomendación, incluyendo casos límite y validación con datos reales. |
| **R2: Incumplimiento de la propuesta de valor** | Si el usuario prioriza “más rápido” y el sistema falla en estimar los tiempos, el producto puede llegar tarde, afectando la experiencia de usuario del cliente. | 3 | 2 | Medio (6) | Implementar un sistema de retroalimentación para que los usuarios puedan reportar discrepancias en los tiempos estimados y ajustar el algoritmo en consecuencia. |
| **R3: Incompatibilidad del modelo del “Proveedor local”** | Empresas como DHL y FedEx tienen sistemas y matrices de precios estandarizados y muy estructurados. Un proveedor local en cambio, puede ser más informal, y el sistema correría riesgos tales como cambios de precio sin aviso o información desactualizada. | 2 | 2 | Medio (4) | Establecer acuerdos claros con los proveedores locales para garantizar la actualización periódica de sus tarifas y condiciones, así como implementar un sistema de monitoreo para detectar cambios en tiempo real. |

### 5.2 Riesgos Técnicos

| Riesgo | Descripción | Impacto | Probabilidad | Riesgo | Mitigación |
| --- | --- | --- | --- | --- | --- |
| **R4: Acoplamiento fuerte a proveedores específicos** | Una implementación poco flexible puede dificultar la escalabilidad (ejemplo: agregar un nuevo proveedor) en futuras versiones. | 3 | 3 | Alto (9) | Diseñar el sistema con una arquitectura modular y orientada a servicios, utilizando interfaces y abstracciones para facilitar la integración de nuevos proveedores sin afectar la lógica central. |
| **R5: Inconsistencia en los datos de entrada** | Si el sistema no gestiona los tipos de datos de forma adecuada (ejemplo: tipos de medidas en peso, origen y destino correctos, tipo de moneda) podría verse afectado el cálculo de las opciones disponibles. | 3 | 2 | Medio (6) | Implementar validaciones estrictas en la entrada de datos, incluyendo formatos, rangos permitidos y tipos de datos, para asegurar que la información ingresada sea consistente y adecuada para el procesamiento. |
| **R6: Manejo insuficiente de casos límite** | Destinos sin cobertura o pesos no admitidos por los proveedores pueden generar fallos si no se controlan adecuadamente. | 3 | 1 | Bajo (3) | Desarrollar un sistema de manejo de errores robusto que identifique y gestione adecuadamente los casos límite, proporcionando mensajes claros al usuario y evitando que el sistema falle ante situaciones no contempladas. |

## Flujo del proyecto 

![Flujo completo del optimizador de envíos](flujo_optimizador_envios_completo.jpg)

