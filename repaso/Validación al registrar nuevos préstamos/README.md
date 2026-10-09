# 📘 EXAMEN 2 — VALIDACIÓN Y REGISTRO DE PRÉSTAMOS

**Proyecto:** Sistema de Cobros de Cartera CrediYa S.A.S.  
**Dificultad:** Intermedia  
**Tecnologías:** Java, POO, excepciones, JDBC, MySQL y colecciones.  

---

## 📁 Estructura del Examen

```text
examen 2/
├── bin/                                       <-- Clases compiladas (.class)
├── src/com/crediya/
│   ├── servicio/
│   │   ├── ValidadorPrestamos.java            <-- Validador de reglas de negocio
│   │   └── GestorRegistroPrestamos.java       <-- Orquestador del registro seguro
│   └── prueba/
│       └── PruebaExamen2.java                 <-- Suite de pruebas de validaciones
├── EVIDENCIAS_EXAMEN_2.md                     <-- Salida real de ejecución en consola
├── PROMPT.md                                  <-- Enunciado oficial del examen
└── README.md                                  <-- Documentación y guía de ejecución
```

---

## 🚀 Requerimientos Implementados

| Punto | Requerimiento | Implementación |
|---|---|---|
| **1** | Validador de préstamos | [`ValidadorPrestamos`](file:///c:/Users/Pc_Ruben/Documents/NetBeansProjects/Examen_Java_Ruben/examen%202/src/com/crediya/servicio/ValidadorPrestamos.java) con método `validarPrestamo(Prestamo)` verificando cliente en BD, empleado, monto > 0, interés entre 0-100%, cuotas entre 1-120 y fecha no nula. |
| **2** | Manejo de excepciones | Excepciones específicas por cada causa (`ClienteNoEncontradoException`, `EmpleadoNoEncontradoException`, `DatosInvalidosException`), sin atrapar excepciones genéricas a ciegas. |
| **3** | Registro seguro del préstamo | [`GestorRegistroPrestamos`](file:///c:/Users/Pc_Ruben/Documents/NetBeansProjects/Examen_Java_Ruben/examen%202/src/com/crediya/servicio/GestorRegistroPrestamos.java) que ejecuta las validaciones, calcula valor total y cuota con `CalculadoraPrestamo`, y persiste vía JDBC solo si todo es correcto. |
| **4** | Integridad de datos | Garantía de que un error en validaciones impide cualquier inserción en MySQL. |
| **5** | Suite y Ejecución | Suite completa [`PruebaExamen2`](file:///c:/Users/Pc_Ruben/Documents/NetBeansProjects/Examen_Java_Ruben/examen%202/src/com/crediya/prueba/PruebaExamen2.java) con casos válidos e inválidos. |

---

## ⚡ Cómo Compilar y Ejecutar

### 1. Compilar:
```powershell
javac -encoding UTF-8 -cp "target/classes" -d "examen 2/bin" "examen 2/src/com/crediya/servicio/ValidadorPrestamos.java" "examen 2/src/com/crediya/servicio/GestorRegistroPrestamos.java" "examen 2/src/com/crediya/prueba/PruebaExamen2.java"
```

### 2. Ejecutar la Suite de Pruebas:
```powershell
java -cp "target/classes;examen 2/bin" com.crediya.prueba.PruebaExamen2
```
