# PROMPT — EXAMEN JAVA: VALIDACIÓN Y REGISTRO DE PRÉSTAMOS

**Proyecto:** Sistema de Cobros de Cartera CrediYa S.A.S.  
**Dificultad:** Intermedia  
**Tecnologías:** Java, POO, excepciones, JDBC, MySQL y colecciones.  

---

## 1. Contexto

Eres un desarrollador junior de CrediYa S.A.S. y debes mejorar el proceso de registro de préstamos del sistema existente.

La empresa necesita garantizar que los préstamos se registren únicamente cuando los datos del cliente, del empleado y de la operación sean válidos.

Tu objetivo es implementar un proceso de validación antes de guardar un préstamo en la base de datos.

**Importante:** debes trabajar sobre el proyecto CrediYa existente. Revisa primero las clases `Prestamo`, `Cliente`, `Empleado` y los componentes responsables de guardar los datos. Adapta las clases existentes en lugar de crear duplicados.

---

## 2. Requerimientos funcionales

### Punto 1. Crear o adaptar un validador

Crea una clase llamada `ValidadorPrestamos` o adapta la estructura de validación que ya exista.

Implementa un método:

```java
validarPrestamo(Prestamo prestamo)
```

Este método debe verificar:

* Que el objeto préstamo no sea nulo.
* Que el cliente asociado exista en la base de datos.
* Que el empleado responsable exista.
* Que el monto del préstamo sea mayor que cero.
* Que la tasa de interés sea válida según las reglas del sistema.
* Que el número de cuotas sea mayor que cero.
* Que la fecha de inicio sea válida.
* Que el cliente esté habilitado para solicitar préstamos, si el sistema maneja estados de clientes.

### Punto 2. Manejo de excepciones

Implementa excepciones o validaciones que permitan identificar cada error.

Por ejemplo:

* Cliente inexistente.
* Empleado inexistente.
* Monto inválido.
* Número de cuotas incorrecto.
* Fecha inválida.
* Error al consultar la base de datos.

Los mensajes deben ser claros y permitir identificar qué dato debe corregirse.

Evita capturar todas las excepciones sin distinguir su causa.

### Punto 3. Registro seguro del préstamo

Adapta el método de registro existente para que ejecute las validaciones antes de insertar el préstamo.

El proceso debe:

1. Recibir los datos del préstamo.
2. Validar la información.
3. Confirmar la existencia del cliente y del empleado.
4. Calcular el valor total y la cuota mensual según las fórmulas y reglas que utilice el proyecto.
5. Guardar el préstamo en MySQL mediante JDBC si todas las validaciones son satisfactorias.
6. Informar al usuario si el registro fue exitoso o si debe corregir algún dato.

Si ocurre un error de validación, no debe guardarse un préstamo inválido.

### Punto 4. Integridad de los datos

Utiliza las relaciones existentes entre préstamos, clientes y empleados.

* No permitas asociar un préstamo a un cliente inexistente.
* No permitas asociarlo a un empleado inexistente.
* Aprovecha las claves foráneas y restricciones de la base de datos.
* Evita registros parciales cuando una operación involucre varias escrituras relacionadas.

Si el proceso necesita ejecutar varias operaciones SQL dependientes, utiliza transacciones JDBC cuando corresponda.

### Punto 5. Integración con el menú

Integra las validaciones en el proceso de creación de préstamos del menú actual.

No es necesario crear un menú nuevo ni cambiar las funcionalidades que ya están operativas.

---

## 3. Ejemplo de ejecución esperado

```text
=== REGISTRO DE PRÉSTAMO ===

Ingrese el ID del cliente: 10
Ingrese el ID del empleado: 2
Ingrese el monto: -500000

Error: el monto del préstamo debe ser mayor que cero.
El préstamo no fue registrado.
```

Ejemplo de registro válido:

```text
=== REGISTRO DE PRÉSTAMO ===

Cliente validado correctamente.
Empleado validado correctamente.
Datos del préstamo validados correctamente.
Préstamo registrado correctamente.
```

Los identificadores y valores son ilustrativos. La implementación debe trabajar con los datos reales del sistema.

---

## 4. Entregables

1. Clase `ValidadorPrestamos` o modificación equivalente.
2. Clases Java modificadas para integrar las validaciones.
3. Ajustes SQL, si son necesarios.
4. Evidencias de casos válidos e inválidos.
5. Archivo `README.md` con los cambios realizados.

---

## 5. Criterios de evaluación

* Diseño de clases y encapsulamiento.
* Validación de clientes, empleados y datos financieros.
* Uso adecuado de excepciones.
* Persistencia correcta con JDBC.
* Integridad de los datos y transacciones cuando corresponda.
* Integración con el módulo de préstamos existente.
* Pruebas y documentación.

---

## 6. Condiciones obligatorias

* No crear un sistema nuevo desde cero.
* No eliminar las validaciones que ya existan.
* No permitir que un error de validación termine guardando un préstamo inválido.
* Reutilizar las conexiones, modelos y DAO disponibles.
* Mantener funcionando los módulos existentes de CrediYa.
