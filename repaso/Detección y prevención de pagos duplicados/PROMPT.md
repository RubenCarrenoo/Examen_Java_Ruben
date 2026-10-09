# PROMPT — EXAMEN JAVA: DETECCIÓN Y PREVENCIÓN DE PAGOS DUPLICADOS

**Proyecto:** Sistema de Cobros de Cartera CrediYa S.A.S.  
**Dificultad:** Intermedia  
**Tecnologías:** Java, POO, ArrayList, Stream API, expresiones lambda, excepciones, MySQL y JDBC.  

---

## 1. Contexto

Eres un desarrollador junior de CrediYa S.A.S. y debes mejorar el módulo de pagos del sistema existente.

La empresa necesita prevenir errores en los que una misma operación de pago se registre más de una vez. Estos duplicados pueden provocar inconsistencias en el saldo de los préstamos y en los reportes de cartera.

Tu tarea consiste en implementar un mecanismo de detección y prevención de pagos duplicados antes de guardar un nuevo pago.

**Importante:** trabaja sobre el proyecto CrediYa existente. Revisa la clase `Pago`, el gestor de pagos, la conexión a MySQL y las operaciones de registro actuales. Reutiliza las clases existentes y no dupliques funcionalidades.

---

## 2. Requerimientos funcionales

### Punto 1. Consultar pagos existentes

Crea o adapta un método que permita consultar los pagos registrados de un préstamo.

Debe:

* Recibir el identificador del préstamo.
* Consultar los pagos existentes en MySQL mediante JDBC.
* Almacenar los resultados en una colección `ArrayList<Pago>` cuando corresponda.
* Manejar errores de conexión y consulta.
* Informar cuando el préstamo no tenga pagos registrados.

### Punto 2. Detectar posibles duplicados

Implementa el método:

```java
validarPagoDuplicado(Pago pago)
```

Para este examen, considera que un pago es duplicado cuando coincide con otro pago activo en los siguientes datos:

* Identificador del préstamo.
* Fecha del pago.
* Monto del pago.

Utiliza Stream API y expresiones lambda para comparar los datos de los pagos consultados.

No consideres los pagos anulados como duplicados activos si el sistema ya tiene un mecanismo para identificarlos.

### Punto 3. Prevenir registros duplicados

Adapta el método de registro de pagos para que:

1. Valide que el objeto pago no sea nulo.
2. Verifique que el préstamo asociado exista.
3. Compruebe que el monto y la fecha sean válidos.
4. Consulte los pagos existentes.
5. Ejecute la validación de duplicados.
6. Guarde el pago en MySQL únicamente cuando cumpla las reglas definidas.
7. Muestre un mensaje cuando el pago ya esté registrado.

### Punto 4. Manejo de excepciones

Implementa un manejo apropiado de los siguientes casos:

* Pago nulo.
* Monto negativo o igual a cero.
* Fecha de pago inválida.
* Préstamo inexistente.
* Pago duplicado.
* Error de conexión a MySQL.
* Error durante la inserción.

Cada error debe mostrar un mensaje comprensible y no debe confundirse una falla de conexión con la confirmación de que un pago está duplicado.

### Punto 5. Evitar inconsistencias

Considera que dos operaciones podrían intentar registrar el mismo pago al mismo tiempo.

Si la estructura de la base de datos lo permite, explica o implementa una estrategia adicional de integridad para reducir los registros duplicados concurrentes. No agregues una restricción única sobre los tres campos sin comprobar que sea compatible con las reglas reales del negocio.

La verificación previa no debe considerarse una garantía absoluta contra registros concurrentes.

### Punto 6. Integración con CrediYa

Integra la prevención de duplicados en el proceso de registro de pagos existente.

* Conserva las funcionalidades de registro y consulta.
* Reutiliza la conexión y los DAO existentes.
* No reemplaces MySQL por almacenamiento exclusivamente en memoria.
* Evita cambios innecesarios en los demás módulos.

---

## 3. Ejemplo de ejecución esperado

```text
=== REGISTRO DE PAGO ===

Préstamo: 3
Fecha: 2025-10-09
Monto: 250000

Error: ya existe un pago activo con el mismo préstamo,
fecha y monto.
No se registró el pago duplicado.
```

Ejemplo de registro válido:

```text
=== REGISTRO DE PAGO ===

Validando información del pago...
Consultando pagos existentes...
No se encontraron duplicados.
Pago registrado correctamente.
```

Los datos son ilustrativos y deben adaptarse al formato de salida del proyecto.

---

## 4. Entregables

1. Clases Java creadas o modificadas.
2. Método de validación de pagos duplicados.
3. Consultas SQL o ajustes de base de datos, si son necesarios.
4. Evidencias de registro válido, duplicado y error de conexión.
5. Archivo `README.md` con instrucciones de ejecución.

---

## 5. Criterios de evaluación

* Correcta implementación de la validación.
* Uso de colecciones, Stream API y expresiones lambda.
* Consultas JDBC y persistencia en MySQL.
* Manejo de excepciones.
* Prevención de inconsistencias.
* Integración con el proyecto existente.
* Pruebas y documentación.

---

## 6. Condiciones obligatorias

* No crear un proyecto independiente.
* No duplicar la clase `Pago` si ya existe.
* No modificar pagos existentes innecesariamente.
* No asumir que dos pagos son duplicados únicamente por tener el mismo monto.
* Mantener funcionando las funcionalidades previas de CrediYa.
* Verificar que el proyecto compile y que la validación funcione con datos reales.
