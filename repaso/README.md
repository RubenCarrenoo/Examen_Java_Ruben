# 🎓 PORTAFOLIO DE EXÁMENES RESUELTOS — CrediYa S.A.S.

Bienvenido a la carpeta de **Repaso y Solución de Exámenes** del proyecto CrediYa. Dentro de esta carpeta encontrarás la implementación completa, probada, documentada y lista para producción de los **tres exámenes prácticos**:

---

## 📂 Estructura General de la Carpeta `repaso/`

```text
repaso/
├── README.md                          <-- (Este archivo) Índice general y guía de ejecución
│
├── examen_a/                          <-- EXAMEN A: Estado de pagos y deuda del cliente
│   ├── src/com/crediya/
│   │   ├── modelo/
│   │   │   ├── EstadoPago.java                (Enum: APROBADO, PENDIENTE, RECHAZADO)
│   │   │   ├── EstadoClienteFinanciero.java   (Enum: PAGADO, PENDIENTE, EN_MORA, SIN_PRESTAMOS)
│   │   │   ├── PagoConEstado.java             (Extiende Pago con soporte de estados)
│   │   │   ├── DetallePrestamoFinanciero.java (DTO del préstamo individual)
│   │   │   └── ResumenEstadoCliente.java      (DTO consolidado del cliente)
│   │   ├── servicio/
│   │   │   └── GestorEstadoPagos.java         (Lógica de negocio y cálculo de deuda)
│   │   └── prueba/
│   │       └── PruebaExamenA.java             (Suite de prueba automatizada)
│   ├── sql/
│   │   └── actualizacion_examen_a.sql         (Script ALTER TABLE para MySQL)
│   ├── README.md                              (Documentación técnica detallada)
│   └── EVIDENCIAS_EXAMEN_A.md                 (Salida de consola de los 4 estados)
│
├── examen_b/                          <-- EXAMEN B: Historial y búsqueda avanzada de pagos
│   ├── src/com/crediya/
│   │   ├── modelo/
│   │   │   ├── EstadoPago.java                (Enum de estados de pago)
│   │   │   ├── PagoConEstado.java             (Modelo de pago con estado)
│   │   │   └── ResumenHistorialPagos.java     (DTO con totales independientes)
│   │   ├── servicio/
│   │   │   └── GestorHistorialPagos.java      (Filtros con Stream API, Lambdas, Comparator)
│   │   └── prueba/
│   │       └── PruebaExamenB.java             (Suite con pruebas de filtros y resumen)
│   ├── datos/
│   │   └── datos_prueba_pagos.sql             (Datos de prueba para MySQL)
│   ├── README.md                              (Documentación técnica detallada)
│   └── EVIDENCIAS_EXAMEN_B.md                 (Evidencias de filtros, orden y totales)
│
└── examen_c/                          <-- EXAMEN C: Reporte de clientes morosos
    ├── src/com/crediya/
    │   ├── modelo/
    │   │   └── DetalleClienteMoroso.java      (DTO con soporte para CSV y tabular)
    │   ├── servicio/
    │   │   └── GestorMorosidad.java           (Detección de mora, Top N, exportación)
    │   └── prueba/
    │       └── PruebaExamenC.java             (Suite de prueba y generación de archivos)
    ├── exportados/
    │   ├── reporte_morosos.csv                (Archivo CSV real generado)
    │   └── reporte_morosos.txt                (Archivo TXT real generado)
    ├── README.md                              (Documentación técnica detallada)
    └── EVIDENCIAS_EXAMEN_C.md                 (Evidencias de morosos, filtros y archivos)
```

---

## 📊 Resumen de los Tres Exámenes

| Examen | Tema Principal | Clase Gestora | Puntos Clave Resueltos |
|---|---|---|---|
| **Examen A** | Estado de Pagos y Deuda del Cliente | [`GestorEstadoPagos`](file:///c:/Users/Pc_Ruben/Documents/NetBeansProjects/Examen_Java_Ruben/repaso/examen_a/src/com/crediya/servicio/GestorEstadoPagos.java) | Clasificación en 4 estados (`PAGADO`, `PENDIENTE`, `EN_MORA`, `SIN_PRESTAMOS`), descuento estricto **solo de pagos aprobados**, saldo nunca negativo. |
| **Examen B** | Historial y Búsqueda Avanzada de Pagos | [`GestorHistorialPagos`](file:///c:/Users/Pc_Ruben/Documents/NetBeansProjects/Examen_Java_Ruben/repaso/examen_b/src/com/crediya/servicio/GestorHistorialPagos.java) | Filtros multicriterio con **Stream API** (fecha, monto mínimo, estado), orden cronológico, y resumen con cálculo disjunto de totales. |
| **Examen C** | Reporte de Clientes Morosos | [`GestorMorosidad`](file:///c:/Users/Pc_Ruben/Documents/NetBeansProjects/Examen_Java_Ruben/repaso/examen_c/src/com/crediya/servicio/GestorMorosidad.java) | Discriminación estricta entre tener deuda y estar en mora, ordenamiento descendente de deuda, `sorted() + limit()`, exportación a `.csv` y `.txt`. |

---

## 🚀 Cómo Ejecutar las Suites de Pruebas

Todas las soluciones fueron compiladas y validadas con Java. Puedes ejecutar las pruebas de consola con los siguientes comandos en PowerShell:

### Ejecutar Examen A:
```powershell
java -cp "target/classes;repaso/examen_a/bin" com.crediya.prueba.PruebaExamenA
```

### Ejecutar Examen B:
```powershell
java -cp "target/classes;repaso/examen_b/bin" com.crediya.prueba.PruebaExamenB
```

### Ejecutar Examen C:
```powershell
java -cp "target/classes;repaso/examen_c/bin" com.crediya.prueba.PruebaExamenC
```
