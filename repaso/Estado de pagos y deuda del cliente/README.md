# 📋 SOLUCIÓN COMPLETA — Examen A: Estado de Pagos y Deuda del Cliente

**Dificultad:** Media  
**Tiempo estimado:** 90 minutos  
**Módulos ampliados:** Préstamos y Pagos (`com.crediya.servicio`, `com.crediya.modelo`, `com.crediya.vista`)  
**Puntuación máxima:** 100 puntos  

---

## 🎯 Objetivo General
Ampliar el sistema **CrediYa S.A.S.** para permitir la consulta consolidada del estado financiero de un cliente y determinar con exactitud cuánto dinero debe, analizando préstamos otorgados y descontando **únicamente los pagos aprobados**.

---

## 🛠️ Requerimientos Técnicos y Solución Implementada

### 1. Clase `GestorEstadoPagos`
* **Ubicación:** Paquete `com.crediya.servicio`.
* **Diseño e Integración:** Inyecta por constructor las dependencias de acceso a datos existentes:
  ```java
  public GestorEstadoPagos(ClienteDAO clienteDAO, PrestamoDAO prestamoDAO, PagoDAO pagoDAO)
  ```
* Respeta la arquitectura en capas: no contiene sentencias SQL manuales ni conexiones separadas; delega en los DAOs que utilizan `ConexionBD`.

---

### 2. Método `consultarEstadoCliente(int clienteId)`
Recupera los datos del cliente y todos sus préstamos, evalúa las cuotas vencidas y los pagos, calculando la deuda total y el estado global.

```java
public ResumenEstadoCliente consultarEstadoCliente(int clienteId) throws CrediYaException
```
* **Validación previa:** Si el ID es menor o igual a cero, lanza `DatosInvalidosException`.
* **Cliente no encontrado:** Si el cliente no existe en la base de datos, lanza `ClienteNoEncontradoException`.
* **Retorno:** Objeto `ResumenEstadoCliente` con el desglose de cada préstamo, deuda total, suma de pagos aprobados y el estado final.

---

### 3. Método `calcularSaldoPendiente(int prestamoId)`
Calcula el saldo de un préstamo aplicando la regla de negocio estricta:

$$\text{Saldo Pendiente} = \max(0.0, \text{Monto Total} - \sum \text{Pagos Aprobados})$$

```java
public double calcularSaldoPendiente(int prestamoId) throws CrediYaException
```
* **Regla estricta:** Los pagos con estado `PENDIENTE` o `RECHAZADO` **no se descuentan**.
* **Protección contra saldo negativo:** Se utiliza `Math.max(0.0, ...)` para asegurar que el saldo nunca sea inferior a cero.

---

### 4. Determinación de los 4 Estados Financieros
El examen exige clasificar la situación del cliente en uno de los siguientes estados:

| Estado | Condición de Negocio |
|---|---|
| `SIN_PRESTAMOS` | El cliente está registrado pero no tiene ningún préstamo otorgado. |
| `PAGADO` | Todos sus préstamos tienen saldo pendiente igual a cero. |
| `PENDIENTE` | Tiene saldo pendiente por pagar, pero todas sus cuotas exigibles a la fecha están cubiertas. |
| `EN_MORA` | Al menos uno de sus préstamos tiene cuotas vencidas impagas (o fue marcado manualmente como `VENCIDO`). |

#### Adaptación Mínima del Modelo Realizada:
1. **Enum `EstadoPago`:** Define los valores `APROBADO`, `PENDIENTE`, `RECHAZADO`.
2. **Clase `PagoConEstado`:** Hereda de `Pago` (`extends Pago`), invoca `super(...)` y añade el atributo `EstadoPago estado`.
3. **Regla de cuotas vencidas:** Se calculan los meses transcurridos con `ChronoUnit.MONTHS.between(fechaInicio, LocalDate.now())` y se compara el monto pagado aprobado contra el valor esperado exigible.

---

## 💻 Integración al Menú de Consola (`MenuConsola.java`)

Para incorporar esta funcionalidad al menú principal, se agregan las siguientes líneas:

### Paso 1: En el constructor de `MenuConsola.java`:
```java
private final GestorEstadoPagos gestorEstadoPagos;

public MenuConsola(..., GestorEstadoPagos gestorEstadoPagos) {
    // ...
    this.gestorEstadoPagos = gestorEstadoPagos;
}
```

### Paso 2: Opción en el submenú de clientes o préstamos:
```java
System.out.println("  8. Consultar estado financiero y deuda del cliente");

// En el switch:
case 8 -> consultarEstadoFinancieroCliente();
```

### Paso 3: Método de interacción:
```java
private void consultarEstadoFinancieroCliente() {
    ejecutar(() -> {
        System.out.println("\n--- ESTADO FINANCIERO DEL CLIENTE ---");
        int clienteId = leerEntero("ID del cliente: ");
        ResumenEstadoCliente resumen = gestorEstadoPagos.consultarEstadoCliente(clienteId);
        System.out.println(resumen.reporteFormateado());
    });
}
```

---

## 🧪 Ejecución de Pruebas y Evidencias

La suite automatizada se encuentra en `com.crediya.prueba.PruebaExamenA`:

```bash
# Compilar:
javac -cp target/classes -d repaso/examen_a/bin (Get-ChildItem -Recurse -Filter *.java -Path repaso/examen_a/src | % { $_.FullName })

# Ejecutar:
java -cp "target/classes;repaso/examen_a/bin" com.crediya.prueba.PruebaExamenA
```

Consulte el detalle completo de las 4 salidas en [`EVIDENCIAS_EXAMEN_A.md`](./EVIDENCIAS_EXAMEN_A.md).
