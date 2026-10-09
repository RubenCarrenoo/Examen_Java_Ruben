# 📸 EVIDENCIAS DE EJECUCIÓN — Examen B

A continuación se documentan las pruebas ejecutadas por la suite `PruebaExamenB.java` sobre el historial del préstamo #201:

---

### Evidencia 1: Consulta General del Historial
```text
1. CONSULTA DE HISTORIAL DEL PRÉSTAMO #201:
   Pago #1 | Préstamo #201 | Fecha: 2026-02-10 | Monto: $330.000 | Estado: APROBADO
   Pago #2 | Préstamo #201 | Fecha: 2026-03-12 | Monto: $330.000 | Estado: APROBADO
   Pago #3 | Préstamo #201 | Fecha: 2026-04-15 | Monto: $500.000 | Estado: RECHAZADO
   Pago #4 | Préstamo #201 | Fecha: 2026-04-20 | Monto: $330.000 | Estado: APROBADO
   Pago #5 | Préstamo #201 | Fecha: 2026-05-10 | Monto: $200.000 | Estado: PENDIENTE
   Pago #6 | Préstamo #201 | Fecha: 2026-06-05 | Monto: $450.000 | Estado: APROBADO
   Total pagos encontrados: 6
```

---

### Evidencia 2: Filtro por Rango de Fechas (Marzo a Abril de 2026)
```text
2. FILTRO POR FECHA (Entre 2026-03-01 y 2026-04-30):
   Pago #2 | Préstamo #201 | Fecha: 2026-03-12 | Monto: $330.000 | Estado: APROBADO
   Pago #3 | Préstamo #201 | Fecha: 2026-04-15 | Monto: $500.000 | Estado: RECHAZADO
   Pago #4 | Préstamo #201 | Fecha: 2026-04-20 | Monto: $330.000 | Estado: APROBADO
```

---

### Evidencia 3: Filtro por Monto Mínimo (Monto >= $350.000)
```text
3. FILTRO POR MONTO MÍNIMO (>= $350.000):
   Pago #3 | Préstamo #201 | Fecha: 2026-04-15 | Monto: $500.000 | Estado: RECHAZADO
   Pago #6 | Préstamo #201 | Fecha: 2026-06-05 | Monto: $450.000 | Estado: APROBADO
```

---

### Evidencia 4: Filtro por Estado (Únicamente APROBADOS)
```text
4. FILTRO POR ESTADO (Solo APROBADO):
   Pago #1 | Préstamo #201 | Fecha: 2026-02-10 | Monto: $330.000 | Estado: APROBADO
   Pago #2 | Préstamo #201 | Fecha: 2026-03-12 | Monto: $330.000 | Estado: APROBADO
   Pago #4 | Préstamo #201 | Fecha: 2026-04-20 | Monto: $330.000 | Estado: APROBADO
   Pago #6 | Préstamo #201 | Fecha: 2026-06-05 | Monto: $450.000 | Estado: APROBADO
```

---

### Evidencia 5: Ordenamiento Cronológico Descendente (Más recientes primero)
```text
5. ORDENAMIENTO CRONOLÓGICO DESCENDENTE (Más recientes primero):
   Pago #6 | Préstamo #201 | Fecha: 2026-06-05 | Monto: $450.000 | Estado: APROBADO
   Pago #5 | Préstamo #201 | Fecha: 2026-05-10 | Monto: $200.000 | Estado: PENDIENTE
   Pago #4 | Préstamo #201 | Fecha: 2026-04-20 | Monto: $330.000 | Estado: APROBADO
   Pago #3 | Préstamo #201 | Fecha: 2026-04-15 | Monto: $500.000 | Estado: RECHAZADO
   Pago #2 | Préstamo #201 | Fecha: 2026-03-12 | Monto: $330.000 | Estado: APROBADO
   Pago #1 | Préstamo #201 | Fecha: 2026-02-10 | Monto: $330.000 | Estado: APROBADO
```

---

### Evidencia 6: Combinación de 2 Filtros (Monto >= $300.000 Y Estado APROBADO)
```text
6. COMBINACIÓN DE FILTROS (Monto >= $300.000 Y Estado APROBADO):
   Pago #1 | Préstamo #201 | Fecha: 2026-02-10 | Monto: $330.000 | Estado: APROBADO
   Pago #2 | Préstamo #201 | Fecha: 2026-03-12 | Monto: $330.000 | Estado: APROBADO
   Pago #4 | Préstamo #201 | Fecha: 2026-04-20 | Monto: $330.000 | Estado: APROBADO
   Pago #6 | Préstamo #201 | Fecha: 2026-06-05 | Monto: $450.000 | Estado: APROBADO
```

---

### Evidencia 7: Resumen Financiero del Historial (Cálculo Disjunto)
```text
7. RESUMEN FINANCIERO DEL HISTORIAL:
--------------------------------------------------------------------
                  RESUMEN DEL HISTORIAL DE PAGOS                    
--------------------------------------------------------------------
Total Pagos Registrados: 6
  • Aprobados:           4  |  Monto: $1.440.000
  • Pendientes:          1  |  Monto: $200.000
  • Rechazados:          1  |  Monto: $500.000
--------------------------------------------------------------------
Monto Total Procesado:   $2.140.000
--------------------------------------------------------------------
[ÉXITO] Todas las consultas, filtros y cálculos del Examen B operan al 100%.
```
