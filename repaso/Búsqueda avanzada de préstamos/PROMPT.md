# PROMPT — EXAMEN JAVA: BÚSQUEDA AVANZADA DE PRÉSTAMOS

**Proyecto:** Sistema de Cobros de Cartera CrediYa S.A.S.  
**Dificultad:** Intermedia  
**Tecnologías:** Java, POO, colecciones, Stream API, expresiones lambda, excepciones, MySQL y JDBC.  

---

## 1. Contexto

Eres un desarrollador junior de CrediYa S.A.S. y debes ampliar el módulo de préstamos del sistema existente. Actualmente, la empresa necesita consultar los préstamos de forma más específica para facilitar el seguimiento de las operaciones de crédito.

Tu tarea consiste en implementar funcionalidades de búsqueda, filtrado y ordenamiento de préstamos, utilizando los conocimientos de programación orientada a objetos, colecciones y conexión a bases de datos.

**Importante:** trabaja sobre el proyecto CrediYa existente. Antes de modificar el código, revisa las clases, los paquetes, los modelos, los DAO y los métodos de consulta disponibles. Reutiliza lo que ya exista y evita duplicar funcionalidades.

---

## 2. Requerimientos funcionales

### Punto 1. Gestión de consultas

Crea o adapta una clase llamada `GestorConsultasPrestamos`.

Esta clase debe permitir:

* Consultar todos los préstamos registrados.
* Buscar un préstamo por su identificador.
* Consultar los préstamos asociados a un cliente específico.
* Mostrar un mensaje cuando no existan resultados.
* Manejar las excepciones que puedan presentarse durante las consultas.

### Punto 2. Búsqueda por rango de montos

Implementa el método:

```java
buscarPrestamosPorMonto(double minimo, double maximo)
```

Este método debe:

* Recibir un monto mínimo y uno máximo.
* Obtener los préstamos disponibles.
* Filtrar los préstamos cuyo monto se encuentre dentro del rango indicado.
* Utilizar Stream API y expresiones lambda.
* Mostrar los resultados en consola.

Valida que los montos sean positivos y que el mínimo no sea superior al máximo.

### Punto 3. Filtrado por estado

Implementa el método:

```java
buscarPrestamosPorEstado(String estado)
```

Debe permitir consultar préstamos por estados válidos que ya utilice el proyecto, por ejemplo, `PENDIENTE` o `PAGADO`.

Utiliza Stream API para realizar el filtrado y controla los estados inválidos.

### Punto 4. Ordenamiento de resultados

Implementa un método que permita ordenar los préstamos:

* Por monto de menor a mayor.
* Por monto de mayor a menor.
* Por fecha de registro, cuando ese campo exista.

Utiliza las herramientas de ordenamiento de Java, preferiblemente Stream API y comparadores.

### Punto 5. Persistencia con JDBC

Las consultas deben utilizar la conexión MySQL existente y respetar la arquitectura del proyecto.

* Reutiliza los DAO o repositorios existentes.
* Utiliza `PreparedStatement` cuando corresponda.
* Cierra correctamente los recursos.
* Maneja los errores de conexión y consulta.
* No reemplaces la base de datos por listas ficticias como solución definitiva.

### Punto 6. Integración

Agrega las opciones necesarias al menú de consola existente para que el usuario pueda realizar las búsquedas.

No elimines ni dañes las funcionalidades actuales de clientes, empleados, préstamos y pagos.

---

## 3. Ejemplo de ejecución esperado

```text
=== CONSULTA AVANZADA DE PRÉSTAMOS ===

Ingrese el monto mínimo: 500000
Ingrese el monto máximo: 3000000

=== RESULTADOS ===
Préstamo ID: 2 - Monto: 800000 - Estado: PENDIENTE
Préstamo ID: 5 - Monto: 2500000 - Estado: PAGADO

Total de préstamos encontrados: 2
```

Los datos mostrados deben provenir de los registros reales de la base de datos.

---

## 4. Entregables

1. Clases Java creadas o modificadas.
2. Consultas SQL o ajustes de base de datos, si son necesarios.
3. Evidencias de las búsquedas y filtros.
4. Archivo `README.md` con instrucciones de ejecución.
5. Proyecto integrado y funcional.

---

## 5. Criterios de evaluación

* Gestión de consultas y organización del código.
* Uso correcto de Stream API y expresiones lambda.
* Consultas a MySQL mediante JDBC.
* Validaciones y manejo de excepciones.
* Ordenamiento y filtrado de resultados.
* Integración con el proyecto existente y documentación.

---

## 6. Condiciones obligatorias

* No crear un proyecto independiente.
* No duplicar las clases existentes de préstamos.
* No utilizar datos de prueba como sustituto permanente de MySQL.
* Mantener la arquitectura y las funcionalidades anteriores.
* Verificar que el código compile y que las búsquedas funcionen.
