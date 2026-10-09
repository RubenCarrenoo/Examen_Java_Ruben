# 📸 EVIDENCIAS DE EJECUCIÓN — Examen A

A continuación se presentan las capturas reales de consola producidas por la suite `PruebaExamenA.java`, demostrando los 4 estados financieros requeridos y la regla de no descuento de pagos no aprobados.

---

### Evidencia 1: Estado `SIN_PRESTAMOS`
```text
--------------------------------------------------------------------
CASO 1: CLIENTE SIN PRÉSTAMOS (Esperado: SIN_PRESTAMOS)
--------------------------------------------------------------------
====================================================================
         ESTADO FINANCIERO Y DEUDA DEL CLIENTE
====================================================================
Cliente:        Pedro Sanchez (#1)
Documento:      1001001
Teléfono:       3001112233
Correo:         pedro@mail.com
--------------------------------------------------------------------
Estado General: SIN_PRESTAMOS
Deuda Total:    $0
Pagos Aprobados:$0
--------------------------------------------------------------------
  (El cliente no tiene préstamos registrados)
====================================================================
```

---

### Evidencia 2: Estado `PENDIENTE`
```text
--------------------------------------------------------------------
CASO 2: CLIENTE AL DÍA CON DEUDA PENDIENTE (Esperado: PENDIENTE)
--------------------------------------------------------------------
====================================================================
         ESTADO FINANCIERO Y DEUDA DEL CLIENTE
====================================================================
Cliente:        Juan Perez (#2)
Documento:      1002002
Teléfono:       3004445566
Correo:         juan@mail.com
--------------------------------------------------------------------
Estado General: PENDIENTE
Deuda Total:    $1.980.000
Pagos Aprobados:$220.000
--------------------------------------------------------------------
Detalle de Préstamos:
  • Préstamo #101 | Total: $2.200.000 | Aprobado: $220.000 | Saldo: $1.980.000 | Cuotas Vencidas: 0 | Estado: PENDIENTE
====================================================================
```

---

### Evidencia 3: Estado `EN_MORA`
```text
--------------------------------------------------------------------
CASO 3: CLIENTE CON CUOTAS VENCIDAS (Esperado: EN_MORA)
--------------------------------------------------------------------
====================================================================
         ESTADO FINANCIERO Y DEUDA DEL CLIENTE
====================================================================
Cliente:        Maria Rodriguez (#3)
Documento:      1003003
Teléfono:       3007778899
Correo:         maria@mail.com
--------------------------------------------------------------------
Estado General: EN_MORA
Deuda Total:    $1.980.000
Pagos Aprobados:$220.000
--------------------------------------------------------------------
Detalle de Préstamos:
  • Préstamo #102 | Total: $2.200.000 | Aprobado: $220.000 | Saldo: $1.980.000 | Cuotas Vencidas: 5 | Estado: EN_MORA
    [Nota: $800.000 en pagos pendientes o rechazados no descontados]
====================================================================
```

---

### Evidencia 4: Estado `PAGADO`
```text
--------------------------------------------------------------------
CASO 4: CLIENTE A PAZ Y SALVO (Esperado: PAGADO)
--------------------------------------------------------------------
====================================================================
         ESTADO FINANCIERO Y DEUDA DEL CLIENTE
====================================================================
Cliente:        Carlos Paz (#4)
Documento:      1004004
Teléfono:       3000001122
Correo:         carlos@mail.com
--------------------------------------------------------------------
Estado General: PAGADO
Deuda Total:    $0
Pagos Aprobados:$1.100.000
--------------------------------------------------------------------
Detalle de Préstamos:
  • Préstamo #103 | Total: $1.100.000 | Aprobado: $1.100.000 | Saldo: $0 | Cuotas Vencidas: 10 | Estado: PAGADO
====================================================================
```

---

### Evidencia 5: Validación de Cálculo de Saldo Pendiente
Demuestra que únicamente los pagos aprobados descuentan deuda:
```text
--------------------------------------------------------------------
CASO 5: VALIDACIÓN DE CÁLCULO DE SALDO PENDIENTE
--------------------------------------------------------------------
Préstamo de María (#102): Total pactado = $2.200.000
Pagos registrados: $220.000 (APROBADO), $500.000 (RECHAZADO), $300.000 (PENDIENTE)
Saldo calculado por el gestor: $1.980.000
¿Solo descontó el pago APROBADO de $220.000? SÍ [OK]

[ÉXITO] Todas las pruebas y los 4 estados financieros fueron validados correctamente.
```
