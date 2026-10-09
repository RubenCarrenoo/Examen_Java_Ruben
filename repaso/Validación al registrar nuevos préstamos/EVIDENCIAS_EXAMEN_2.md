# 📋 EVIDENCIAS DE EJECUCIÓN — EXAMEN 2: VALIDACIÓN Y REGISTRO DE PRÉSTAMOS

**Proyecto:** Sistema de Cobros de Cartera CrediYa S.A.S.  
**Fecha de ejecución:** 2026-10-09  
**Clase ejecutada:** `com.crediya.prueba.PruebaExamen2`  

---

## 💻 Salida Real de la Consola

```text
====================================================================
   SUITE DE PRUEBAS AUTOMATIZADA — EXAMEN 2: VALIDACIÓN Y REGISTRO  
====================================================================

--------------------------------------------------------------------
CASO 1: REGISTRO VÁLIDO (Cliente 10, Empleado 2, Monto 2.000.000)
--------------------------------------------------------------------

=== REGISTRO DE PRÉSTAMO ===
Cliente validado correctamente.
Empleado validado correctamente.
Datos del préstamo validados correctamente.
Monto total calculado: $2.200.000
Cuota mensual calculada: $183.333,33
Préstamo registrado correctamente con ID: 51
ID Asignado: 51
Estado Inicial: PENDIENTE

--------------------------------------------------------------------
CASO 2: MONTO NEGATIVO (-$500.000)
--------------------------------------------------------------------
Ingrese el ID del cliente: 10
Ingrese el ID del empleado: 2
Ingrese el monto: -500000

=== REGISTRO DE PRÉSTAMO ===

Error: El monto del préstamo debe ser mayor a cero.
El préstamo no fue registrado.
[Captura exitosa] DatosInvalidosException -> El monto del préstamo debe ser mayor a cero.

--------------------------------------------------------------------
CASO 3: CLIENTE INEXISTENTE (ID 999)
--------------------------------------------------------------------

=== REGISTRO DE PRÉSTAMO ===

Error: El cliente con ID 999 no existe en la base de datos.
El préstamo no fue registrado.
[Captura exitosa] ClienteNoEncontradoException -> El cliente con ID 999 no existe en la base de datos.

--------------------------------------------------------------------
CASO 4: EMPLEADO INEXISTENTE (ID 888)
--------------------------------------------------------------------

=== REGISTRO DE PRÉSTAMO ===

Error: El empleado con ID 888 no existe en la base de datos.
El préstamo no fue registrado.
[Captura exitosa] EmpleadoNoEncontradoException -> El empleado con ID 888 no existe en la base de datos.

--------------------------------------------------------------------
CASO 5: CUOTAS INVÁLIDAS (0 cuotas)
--------------------------------------------------------------------

=== REGISTRO DE PRÉSTAMO ===

Error: El número de cuotas debe estar entre 1 y 120.
El préstamo no fue registrado.
[Captura exitosa] DatosInvalidosException -> El número de cuotas debe estar entre 1 y 120.

--------------------------------------------------------------------
CASO 6: INTERÉS INVÁLIDO (150%)
--------------------------------------------------------------------

=== REGISTRO DE PRÉSTAMO ===

Error: El interés debe estar entre 0 y 100 (%).
El préstamo no fue registrado.
[Captura exitosa] DatosInvalidosException -> El interés debe estar entre 0 y 100 (%).

--------------------------------------------------------------------
CASO 7: VALIDACIÓN DE OBJETO NULO
--------------------------------------------------------------------
[Captura exitosa] DatosInvalidosException -> El préstamo no puede ser nulo.

--------------------------------------------------------------------
VERIFICACIÓN DE INTEGRIDAD DE LA BASE DE DATOS
--------------------------------------------------------------------
Total de préstamos efectivamente guardados: 1
-> [CONFIRMADO] Solo se guardó el préstamo válido. Ningún préstamo erróneo fue insertado.

====================================================================
  [ÉXITO] TODAS LAS PRUEBAS DEL EXAMEN 2 COMPLETADAS SATISFACTORIAMENTE
====================================================================
```

---

## 🎯 Conclusión de Cumplimiento

* **Punto 1:** Clase `ValidadorPrestamos` con el método `validarPrestamo(Prestamo prestamo)` implementado con todas las verificaciones solicitadas.
* **Punto 2:** Excepciones específicas por cada causa (`DatosInvalidosException`, `ClienteNoEncontradoException`, `EmpleadoNoEncontradoException`).
* **Punto 3:** `GestorRegistroPrestamos` valida antes de llamar al DAO e informa al usuario.
* **Punto 4:** Integridad absoluta: ante cualquier error no se persiste nada.
* **Punto 5:** Integración completa y pruebas verificadas.
