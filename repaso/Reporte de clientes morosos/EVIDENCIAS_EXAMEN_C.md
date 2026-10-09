# 📸 EVIDENCIAS DE EJECUCIÓN — Examen C

A continuación se presentan las evidencias de la suite `PruebaExamenC.java`, demostrando la correcta discriminación de clientes al día vs. morosos, los filtros de ordenamiento y la exportación física a disco.

---

### Evidencia 1: Identificación y Exclusión de Clientes no Morosos
Se demuestra que el cliente *Bernardo*, a pesar de tener una deuda activa de $2.200.000, **no es incluido en morosos** porque sus cuotas aún no han vencido:

```text
1. IDENTIFICACIÓN DE CLIENTES MOROSOS:
   Clientes detectados en mora: 2
   • Carlos Moroso Moderado (Doc: 1003)
   • Diana Morosa Critica (Doc: 1004)
   ¿Bernardo (con deuda pero al día) fue excluido de morosos? SÍ [CORRECTO]
```

---

### Evidencia 2: Detalle Consolidado de Morosos
```text
2. DETALLE CONSOLIDADO DE MOROSOS:
   Doc: 1003 | Carlos Moroso Moderado | Préstamos: 1 | Deuda: $1.980.000 | Cuotas Vencidas: 4
   Doc: 1004 | Diana Morosa Critica | Préstamos: 1 | Deuda: $5.500.000 | Cuotas Vencidas: 6
```

---

### Evidencia 3: Ordenamiento de Mayor a Menor Deuda
```text
3. ORDENADOS DE MAYOR A MENOR DEUDA:
   Doc: 1004 | Diana Morosa Critica | Préstamos: 1 | Deuda: $5.500.000 | Cuotas Vencidas: 6
   Doc: 1003 | Carlos Moroso Moderado | Préstamos: 1 | Deuda: $1.980.000 | Cuotas Vencidas: 4
```

---

### Evidencia 4: Top 1 Mayor Deuda (`Stream.sorted().limit(1)`)
```text
4. TOP 1 CLIENTE CON MAYOR DEUDA (Stream.sorted().limit(1)):
   ★ Doc: 1004 | Diana Morosa Critica | Préstamos: 1 | Deuda: $5.500.000 | Cuotas Vencidas: 6
```

---

### Evidencia 5: Filtro por Deuda Mínima (>= $3.000.000)
```text
5. FILTRO POR DEUDA MÍNIMA (>= $3.000.000):
   Doc: 1004 | Diana Morosa Critica | Préstamos: 1 | Deuda: $5.500.000 | Cuotas Vencidas: 6
```

---

### Evidencia 6: Archivos Exportados en Disco
Los archivos se crearon satisfactoriamente en `repaso/examen_c/exportados/`:

#### Contenido del archivo `reporte_morosos.txt`:
```text
=========================================================================================
                     REPORTE DE CLIENTES MOROSOS — CrediYa S.A.S.                         
=========================================================================================
| DOCUMENTO    | NOMBRE CLIENTE           | PRÉSTAMOS P. | DEUDA TOTAL      | CUOTAS VENCIDAS |
-----------------------------------------------------------------------------------------
| 1003         | Carlos Moroso Moderado   | 1            | $1.980.000       | 4               |
| 1004         | Diana Morosa Critica     | 1            | $5.500.000       | 6               |
=========================================================================================
Total Morosos: 2  |  Total Cartera Vencida: $7.480.000
```

#### Contenido del archivo `reporte_morosos.csv`:
```text
#documento;nombre;prestamos_pendientes;deuda_total;cuotas_vencidas
1003;Carlos Moroso Moderado;1;1980000.00;4
1004;Diana Morosa Critica;1;5500000.00;6
```
