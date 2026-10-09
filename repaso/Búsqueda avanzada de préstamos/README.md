# 📘 EXAMEN 1 — BÚSQUEDA AVANZADA DE PRÉSTAMOS

**Proyecto:** Sistema de Cobros de Cartera CrediYa S.A.S.  
**Dificultad:** Intermedia  
**Tecnologías:** Java, POO, colecciones, Stream API, expresiones lambda, excepciones, MySQL y JDBC.  

---

## 📁 Estructura del Examen

```text
examen 1/
├── bin/                                       <-- Clases compiladas (.class)
├── src/com/crediya/
│   ├── servicio/
│   │   └── GestorConsultasPrestamos.java      <-- Clase principal requerida
│   └── prueba/
│       └── PruebaExamen1.java                 <-- Suite de pruebas automatizada
├── EVIDENCIAS_EXAMEN_1.md                     <-- Salida real de ejecución en consola
├── PROMPT.md                                  <-- Enunciado oficial del examen
└── README.md                                  <-- Documentación y guía de ejecución
```

---

## 🚀 Requerimientos Implementados

| Punto | Requerimiento | Implementación |
|---|---|---|
| **1** | Gestión de consultas | `consultarTodos()`, `buscarPorId(int)`, `consultarPorCliente(int)`. Manejo de resultados vacíos y excepciones. |
| **2** | Búsqueda por rango de montos | `buscarPrestamosPorMonto(minimo, maximo)` con **Stream API** y expresiones lambda. Valida que los montos sean positivos y `minimo <= maximo`. |
| **3** | Filtrado por estado | `buscarPrestamosPorEstado(String)` filtrando por estados válidos (`PENDIENTE`, `PAGADO`, `VENCIDO`) con Stream API y validación de estados inexistentes. |
| **4** | Ordenamiento de resultados | `ordenarPorMonto(lista, asc/desc)` y `ordenarPorFecha(lista, asc/desc)` usando `Comparator` y Stream API. |
| **5** | Persistencia JDBC | Integración directa con [`PrestamoDAO`](file:///c:/Users/Pc_Ruben/Documents/NetBeansProjects/Examen_Java_Ruben/src/main/java/com/crediya/dao/PrestamoDAO.java) y [`ConexionBD`](file:///c:/Users/Pc_Ruben/Documents/NetBeansProjects/Examen_Java_Ruben/src/main/java/com/crediya/persistencia/ConexionBD.java). |
| **6** | Suite y Ejecución | Suite completa [`PruebaExamen1`](file:///c:/Users/Pc_Ruben/Documents/NetBeansProjects/Examen_Java_Ruben/examen%201/src/com/crediya/prueba/PruebaExamen1.java) autónoma y lista para probar. |

---

## ⚡ Cómo Compilar y Ejecutar

### 1. Compilar:
```powershell
javac -encoding UTF-8 -cp "target/classes" -d "examen 1/bin" "examen 1/src/com/crediya/servicio/GestorConsultasPrestamos.java" "examen 1/src/com/crediya/prueba/PruebaExamen1.java"
```

### 2. Ejecutar la Suite de Pruebas:
```powershell
java -cp "target/classes;examen 1/bin" com.crediya.prueba.PruebaExamen1
```
