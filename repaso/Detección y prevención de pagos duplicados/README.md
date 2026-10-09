# 📘 EXAMEN 3 — DETECCIÓN Y PREVENCIÓN DE PAGOS DUPLICADOS

**Proyecto:** Sistema de Cobros de Cartera CrediYa S.A.S.  
**Dificultad:** Intermedia  
**Tecnologías:** Java, POO, ArrayList, Stream API, expresiones lambda, excepciones, MySQL y JDBC.  

---

## 📁 Estructura del Examen

```text
examen 3/
├── bin/                                       <-- Clases compiladas (.class)
├── src/com/crediya/
│   ├── excepciones/
│   │   └── PagoDuplicadoException.java        <-- Excepción específica para duplicados
│   ├── servicio/
│   │   └── GestorPrevencionPagosDuplicados.java <-- Lógica de detección y registro seguro
│   └── prueba/
│       └── PruebaExamen3.java                 <-- Suite de pruebas automatizada
├── sql/
│   └── restriccion_pagos_duplicados.sql       <-- Estrategia SQL para concurrencia
├── EVIDENCIAS_EXAMEN_3.md                     <-- Salida real de ejecución en consola
├── PROMPT.md                                  <-- Enunciado oficial del examen
└── README.md                                  <-- Documentación y guía de ejecución
```

---

## 🚀 Requerimientos Implementados

| Punto | Requerimiento | Implementación |
|---|---|---|
| **1** | Consultar pagos existentes | `consultarPagosPorPrestamo(int prestamoId)` con JDBC devolviendo `ArrayList<Pago>`. Maneja listas vacías y errores de conexión. |
| **2** | Detectar pagos duplicados | `validarPagoDuplicado(Pago pago)` utilizando **Stream API** y expresiones **lambda** (`anyMatch`) comparando préstamo, fecha y monto. |
| **3** | Prevenir registros duplicados | `registrarPagoSeguro(...)` orquesta validación previa, consulta de pagos, validación de duplicados y persistencia transaccional. |
| **4** | Manejo de excepciones | Excepción `PagoDuplicadoException` independiente de fallas de conexión (`ErrorPersistenciaException`) o montos inválidos (`DatosInvalidosException`). |
| **5** | Integridad y Concurrencia | Estrategia de concurrencia en Java (`synchronized`) y script SQL de restricción única en [`restriccion_pagos_duplicados.sql`](file:///c:/Users/Pc_Ruben/Documents/NetBeansProjects/Examen_Java_Ruben/examen%203/sql/restriccion_pagos_duplicados.sql). |
| **6** | Suite y Ejecución | Suite completa [`PruebaExamen3`](file:///c:/Users/Pc_Ruben/Documents/NetBeansProjects/Examen_Java_Ruben/examen%203/src/com/crediya/prueba/PruebaExamen3.java) probando casos legítimos, duplicados y errores financieros. |

---

## ⚡ Cómo Compilar y Ejecutar

### 1. Compilar:
```powershell
javac -encoding UTF-8 -cp "target/classes" -d "examen 3/bin" "examen 3/src/com/crediya/excepciones/PagoDuplicadoException.java" "examen 3/src/com/crediya/servicio/GestorPrevencionPagosDuplicados.java" "examen 3/src/com/crediya/prueba/PruebaExamen3.java"
```

### 2. Ejecutar la Suite de Pruebas:
```powershell
java -cp "target/classes;examen 3/bin" com.crediya.prueba.PruebaExamen3
```
