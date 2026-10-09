# 📋 SOLUCIÓN COMPLETA — Examen B: Historial y Búsqueda Avanzada de Pagos

**Dificultad:** Media  
**Tiempo estimado:** 90 minutos  
**Módulos ampliados:** Pagos, Préstamos y Consultas (`com.crediya.servicio`, `com.crediya.modelo`, `com.crediya.vista`)  
**Puntuación máxima:** 100 puntos  

---

## 🎯 Objetivo General
Ampliar el subsistema de pagos de **CrediYa S.A.S.** para permitir la consulta flexible, el filtrado multicriterio y el ordenamiento cronológico del historial de abonos de los préstamos, haciendo uso intensivo de **Stream API y Expresiones Lambda**, además de generar resúmenes disjuntos por estado.

---

## 🛠️ Métodos Implementados en `GestorHistorialPagos`

### 1. `obtenerPagosPorPrestamo(int prestamoId)`
Recupera los pagos del préstamo solicitado tras verificar que el préstamo exista en el sistema.
```java
public List<PagoConEstado> obtenerPagosPorPrestamo(int prestamoId) throws CrediYaException
```
* **Excepción controlada:** Lanza `PrestamoNoEncontradoException` si el ID no existe en MySQL.

---

### 2. Filtros con Stream API

#### A. Filtrar por rango de fechas:
```java
public List<PagoConEstado> filtrarPorFecha(List<PagoConEstado> pagos, LocalDate desde, LocalDate hasta)
```
* **Lógica con Stream:**
  ```java
  pagos.stream()
       .filter(p -> !p.getFechaPago().isBefore(desde) && !p.getFechaPago().isAfter(hasta))
       .toList();
  ```
* **Validación:** Lanza `DatosInvalidosException` si `desde.isAfter(hasta)`.

#### B. Filtrar por monto mínimo:
```java
public List<PagoConEstado> filtrarPorMontoMinimo(List<PagoConEstado> pagos, double montoMinimo)
```
* **Lógica con Stream:**
  ```java
  pagos.stream()
       .filter(p -> p.getMonto() >= montoMinimo)
       .toList();
  ```

#### C. Filtrar por estado específico:
```java
public List<PagoConEstado> filtrarPorEstado(List<PagoConEstado> pagos, EstadoPago estado)
```
* **Lógica con Stream:**
  ```java
  pagos.stream()
       .filter(p -> p.getEstado() == estado)
       .toList();
  ```

#### D. Ordenar cronológicamente:
```java
public List<PagoConEstado> ordenarPorFecha(List<PagoConEstado> pagos, boolean ascendente)
```
* **Lógica con Comparator:**
  ```java
  Comparator<PagoConEstado> comp = Comparator.comparing(PagoConEstado::getFechaPago)
                                             .thenComparingInt(PagoConEstado::getId);
  if (!ascendente) comp = comp.reversed();
  return pagos.stream().sorted(comp).toList();
  ```

---

### 3. Generación del Resumen Disjunto (`generarResumen`)
Calcula en variables independientes la cantidad y el valor acumulado de pagos **Aprobados**, **Pendientes** y **Rechazados**, garantizando que ningún pago sea sumado en más de una categoría contable.

---

## 💻 Integración al Menú de Consola (`MenuConsola.java`)

```java
private void consultarHistorialPagosConFiltros() {
    ejecutar(() -> {
        int prestamoId = leerEntero("Ingrese ID del préstamo: ");
        List<PagoConEstado> pagos = gestorHistorialPagos.obtenerPagosPorPrestamo(prestamoId);

        System.out.println("Opciones de filtro:");
        System.out.println("1. Por rango de fechas");
        System.out.println("2. Por monto mínimo");
        System.out.println("3. Por estado (APROBADO/PENDIENTE/RECHAZADO)");
        System.out.println("4. Aplicar filtros combinados (Fecha + Estado)");

        // Aplica los filtros elegidos e imprime el listado resultante junto con generarResumen(pagosFiltrados)
    });
}
```

---

## 🧪 Pruebas y Evidencias
Consulte [`EVIDENCIAS_EXAMEN_B.md`](./EVIDENCIAS_EXAMEN_B.md) para ver la salida completa de los filtros simples, combinados y el resumen de totales.
