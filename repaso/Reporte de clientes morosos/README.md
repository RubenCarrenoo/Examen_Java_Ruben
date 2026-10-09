# 📋 SOLUCIÓN COMPLETA — Examen C: Reporte de Clientes Morosos

**Dificultad:** Intermedia  
**Tiempo estimado:** 100–120 minutos  
**Módulos ampliados:** Clientes, Préstamos, Pagos y Reportes (`com.crediya.servicio`, `com.crediya.modelo`, `com.crediya.vista`)  
**Puntuación máxima:** 100 puntos  

---

## 🎯 Objetivo General
Ampliar el subsistema de reportes y cartera de **CrediYa S.A.S.** para detectar de forma automatizada a aquellos clientes que posean **obligaciones vencidas e impagas**, calculando el valor total de deuda a recuperar, aplicando filtros avanzados con **Stream API** (`sorted`, `limit`, `filter`), y permitiendo la **exportación de resultados a archivos CSV y texto (.txt)**.

---

## 🛠️ Métodos Implementados en `GestorMorosidad`

### 1. `obtenerClientesMorosos()`
Devuelve la lista única de clientes con al menos una cuota vencida sin pagar.
```java
public List<Cliente> obtenerClientesMorosos() throws ErrorPersistenciaException
```
* **Regla estricta:** **NO se considera moroso a un cliente únicamente por tener saldo pendiente.** Si un cliente tiene una deuda activa pero sus cuotas exigibles a la fecha están cubiertas, es clasificado como *al día* y se excluye del reporte.

---

### 2. `calcularDeudaCliente(int clienteId)`
Suma los saldos pendientes reales de todos los préstamos del cliente descontando los pagos válidos.
```java
public double calcularDeudaCliente(int clienteId) throws ErrorPersistenciaException
```

---

### 3. Filtros y Procesamiento con Stream API

#### A. Filtrar por deuda mínima:
```java
public List<DetalleClienteMoroso> filtrarPorDeudaMinima(List<DetalleClienteMoroso> morosos, double valorMinimo)
```
* Filtra morosos cuya deuda total sea mayor o igual al valor especificado.

#### B. Ordenar de mayor a menor deuda (descendente):
```java
public List<DetalleClienteMoroso> ordenarMorososPorDeuda(List<DetalleClienteMoroso> morosos)
```
* **Código con Stream:**
  ```java
  morosos.stream()
         .sorted(Comparator.comparingDouble(DetalleClienteMoroso::getDeudaTotal).reversed())
         .toList();
  ```

#### C. Obtener Top N clientes morosos:
```java
public List<DetalleClienteMoroso> obtenerTopClientesMorosos(List<DetalleClienteMoroso> morosos, int limite)
```
* **Código con Stream (`sorted` + `limit`):**
  ```java
  morosos.stream()
         .sorted(Comparator.comparingDouble(DetalleClienteMoroso::getDeudaTotal).reversed())
         .limit(limite)
         .toList();
  ```

---

### 4. `exportarReporte(List<DetalleClienteMoroso> morosos, Path rutaArchivo, boolean formatoCsv)`
Permite guardar los resultados en el disco local:
* **Formato CSV (`.csv`):** Cabecera `#documento;nombre;prestamos_pendientes;deuda_total;cuotas_vencidas` con campos separados por punto y coma.
* **Formato Texto Legible (`.txt`):** Tabla con columnas formateadas, alineaciones y gran total de cartera vencida.
* Utiliza `java.nio.file.Files.write` con codificación `UTF-8` y manejo de excepciones con `ErrorPersistenciaException`.

---

## 💻 Integración al Menú de Consola (`MenuConsola.java`)

```java
private void menuReporteMorosos() {
    ejecutar(() -> {
        System.out.println("\n--- REPORTE DE CLIENTES MOROSOS ---");
        List<DetalleClienteMoroso> morosos = gestorMorosidad.obtenerDetalleMorosos();

        if (morosos.isEmpty()) {
            System.out.println("¡Excelente noticia! No existen clientes morosos en la cartera.");
            return;
        }

        System.out.println("1. Ver todos ordenados por deuda");
        System.out.println("2. Ver Top 5 clientes con mayor mora");
        System.out.println("3. Filtrar por deuda mínima");
        System.out.println("4. Exportar reporte a archivo (.csv o .txt)");
        // Lógica de interacción y llamadas al gestor
    });
}
```

---

## 🧪 Pruebas y Evidencias
Consulte [`EVIDENCIAS_EXAMEN_C.md`](./EVIDENCIAS_EXAMEN_C.md) para verificar las salidas de consola y el contenido de los archivos generados en `repaso/examen_c/exportados/`.
