# 📋 EVIDENCIAS DE EJECUCIÓN — EXAMEN 3: PREVENCIÓN DE PAGOS DUPLICADOS

**Proyecto:** Sistema de Cobros de Cartera CrediYa S.A.S.  
**Fecha de ejecución:** 2026-10-09  
**Clase ejecutada:** `com.crediya.prueba.PruebaExamen3`  

---

## 💻 Salida Real de la Consola

```text
====================================================================
  SUITE DE PRUEBAS AUTOMATIZADA — EXAMEN 3: PREVENCIÓN DE DUPLICADOS
====================================================================

--------------------------------------------------------------------
PRUEBA 1: CONSULTAR PAGOS INICIALES (Préstamo #3)
--------------------------------------------------------------------
El préstamo #3 no tiene pagos registrados actualmente.
Cantidad de pagos encontrados: 0

--------------------------------------------------------------------
PRUEBA 2: REGISTRO VÁLIDO DE PRIMER PAGO ($250.000)
--------------------------------------------------------------------

=== REGISTRO DE PAGO ===
Préstamo: 3
Fecha: 2025-10-09
Monto: $250.000

Validando información del pago...
Consultando pagos existentes...
El préstamo #3 no tiene pagos registrados actualmente.
No se encontraron duplicados.
Pago registrado correctamente. Nuevo saldo: $1.950.000
-> Pago guardado con ID: 101

--------------------------------------------------------------------
PRUEBA 3: INTENTO DE REGISTRAR EXACTAMENTE EL MISMO PAGO (DUPLICADO)
--------------------------------------------------------------------

=== REGISTRO DE PAGO ===
Préstamo: 3
Fecha: 2025-10-09
Monto: $250.000

Validando información del pago...
Consultando pagos existentes...
Error: Ya existe un pago activo con el mismo préstamo (3), fecha (2025-10-09) y monto ($250.000).
No se registró el pago duplicado.
[Captura exitosa] -> PagoDuplicadoException: Ya existe un pago activo con el mismo préstamo (3), fecha (2025-10-09) y monto ($250.000).

--------------------------------------------------------------------
PRUEBA 4: PAGO CON MISMO MONTO PERO DISTINTA FECHA (2025-10-10)
--------------------------------------------------------------------

=== REGISTRO DE PAGO ===
Préstamo: 3
Fecha: 2025-10-10
Monto: $250.000

Validando información del pago...
Consultando pagos existentes...
No se encontraron duplicados.
Pago registrado correctamente. Nuevo saldo: $1.700.000
-> [ÉXITO] Pago legítimo aceptado con ID: 102

--------------------------------------------------------------------
PRUEBA 5: EXCEPCIONES FINANCIERAS Y DE PERSISTENCIA
--------------------------------------------------------------------
A) Monto negativo (-50.000):

=== REGISTRO DE PAGO ===
Préstamo: 3
Fecha: 2025-10-09
Monto: $-50.000

[OK Excepción esperada] El monto del pago debe ser mayor a cero.

B) Préstamo inexistente (ID 999):

=== REGISTRO DE PAGO ===
Préstamo: 999
Fecha: 2025-10-09
Monto: $100.000

[OK Excepción esperada] No existe un préstamo con ID 999

--------------------------------------------------------------------
VERIFICACIÓN DE PAGOS REGISTRADOS FINALES
--------------------------------------------------------------------
Total de pagos en base de datos: 2
  * Pago #101 | Préstamo #3 | Fecha: 2025-10-09 | Monto: $250.000
  * Pago #102 | Préstamo #3 | Fecha: 2025-10-10 | Monto: $250.000

-> [CONFIRMADO] Se registraron únicamente los 2 pagos legítimos.
-> El pago duplicado fue rechazado y no alteró el saldo del crédito.

====================================================================
  [ÉXITO] TODAS LAS PRUEBAS DEL EXAMEN 3 COMPLETADAS SATISFACTORIAMENTE
====================================================================
```

---

## 🎯 Conclusión de Cumplimiento

* **Punto 1:** `consultarPagosPorPrestamo` consulta en MySQL mediante JDBC devolviendo `ArrayList<Pago>`.
* **Punto 2:** `validarPagoDuplicado(Pago)` compara por `prestamoId`, `fechaPago` y `monto` usando **Stream API** y expresiones **lambda**.
* **Punto 3:** Registro seguro con validación paso a paso e integridad atómica.
* **Punto 4:** Excepción específica `PagoDuplicadoException` diferenciada de fallas de conexión y validación.
* **Punto 5:** Estrategia de integridad contra registros concurrentes detallada en código y script SQL.
* **Punto 6:** Integración completa y pruebas reproducibles.
