# 📋 EVIDENCIAS DE EJECUCIÓN — EXAMEN 1: BÚSQUEDA AVANZADA DE PRÉSTAMOS

**Proyecto:** Sistema de Cobros de Cartera CrediYa S.A.S.  
**Fecha de ejecución:** 2026-10-09  
**Clase ejecutada:** `com.crediya.prueba.PruebaExamen1`  

---

## 💻 Salida Real de la Consola

```text
====================================================================
       SUITE DE PRUEBAS AUTOMATIZADA — EXAMEN 1: BÚSQUEDA AVANZADA  
====================================================================

--------------------------------------------------------------------
PRUEBA 1: CONSULTA GENERAL Y BÚSQUEDA POR IDENTIFICADOR
--------------------------------------------------------------------
Total de préstamos registrados: 5

Buscando Préstamo con ID 2:
-> Encontrado: Préstamo #2 | Cliente: Ana Martinez | Empleado: Laura Asesora | Monto: $800.000 | Total: $896.000 | Saldo: $896.000 | Estado: PENDIENTE

Buscando Préstamo inexistente (ID 999):
No se encontró ningún préstamo con el ID: 999
-> ¿Existe?: false

--------------------------------------------------------------------
PRUEBA 2: CONSULTA DE PRÉSTAMOS ASOCIADOS A UN CLIENTE
--------------------------------------------------------------------
Préstamos de cliente ID 2 (Ana Martinez):
  * Préstamo #2 | Cliente: Ana Martinez | Empleado: Laura Asesora | Monto: $800.000 | Total: $896.000 | Saldo: $896.000 | Estado: PENDIENTE
  * Préstamo #5 | Cliente: Ana Martinez | Empleado: Laura Asesora | Monto: $2.500.000 | Total: $2.750.000 | Saldo: $0 | Estado: PAGADO

Préstamos de cliente inexistente (ID 50):
[OK Excepción esperada] No existe un cliente con ID 50

--------------------------------------------------------------------
PRUEBA 3: BÚSQUEDA POR RANGO DE MONTOS (Stream API & Lambdas)
--------------------------------------------------------------------
=== CONSULTA AVANZADA DE PRÉSTAMOS ===
Ingrese el monto mínimo: 500000
Ingrese el monto máximo: 3000000

=== RESULTADOS (Rango de montos [$500.000 - $3.000.000]) ===
Préstamo ID: 2 - Monto: $800.000 - Estado: PENDIENTE - Cliente: Ana Martinez
Préstamo ID: 4 - Monto: $1.200.000 - Estado: VENCIDO - Cliente: Roberto Silva
Préstamo ID: 5 - Monto: $2.500.000 - Estado: PAGADO - Cliente: Ana Martinez
Total de préstamos encontrados: 3

Probando validación: Mínimo mayor que máximo (4.000.000 > 1.000.000):
[OK Excepción esperada] El monto mínimo ($4.000.000) no puede ser superior al máximo ($1.000.000).

Probando validación: Montos negativos (-100):
[OK Excepción esperada] Los montos de búsqueda deben ser mayores o iguales a cero.

--------------------------------------------------------------------
PRUEBA 4: FILTRADO POR ESTADO (Stream API)
--------------------------------------------------------------------
Filtrando préstamos PENDIENTE:

=== RESULTADOS (Estado: PENDIENTE) ===
Préstamo ID: 1 - Monto: $300.000 - Estado: PENDIENTE - Cliente: Carlos Gomez
Préstamo ID: 2 - Monto: $800.000 - Estado: PENDIENTE - Cliente: Ana Martinez
Préstamo ID: 3 - Monto: $5.000.000 - Estado: PENDIENTE - Cliente: Carlos Gomez
Total de préstamos encontrados: 3

Filtrando préstamos PAGADO:

=== RESULTADOS (Estado: PAGADO) ===
Préstamo ID: 5 - Monto: $2.500.000 - Estado: PAGADO - Cliente: Ana Martinez
Total de préstamos encontrados: 1

Probando estado inválido ('CANCELADO'):
[OK Excepción esperada] Estado inválido: 'CANCELADO'. Estados válidos permitidos: [PENDIENTE, PAGADO, VENCIDO].

--------------------------------------------------------------------
PRUEBA 5: ORDENAMIENTO (Comparator & Stream API)
--------------------------------------------------------------------
A) Ordenado por Monto de MENOR a MAYOR:
  * ID 1 - $300.000 (Carlos Gomez)
  * ID 2 - $800.000 (Ana Martinez)
  * ID 4 - $1.200.000 (Roberto Silva)
  * ID 5 - $2.500.000 (Ana Martinez)
  * ID 3 - $5.000.000 (Carlos Gomez)

B) Ordenado por Monto de MAYOR a MENOR:
  * ID 3 - $5.000.000 (Carlos Gomez)
  * ID 5 - $2.500.000 (Ana Martinez)
  * ID 4 - $1.200.000 (Roberto Silva)
  * ID 2 - $800.000 (Ana Martinez)
  * ID 1 - $300.000 (Carlos Gomez)

C) Ordenado por Fecha de inicio (más antiguo a más reciente):
  * ID 3 - Fecha: 2024-06-01 - Monto: $5.000.000
  * ID 5 - Fecha: 2024-11-05 - Monto: $2.500.000
  * ID 1 - Fecha: 2025-01-10 - Monto: $300.000
  * ID 4 - Fecha: 2025-02-20 - Monto: $1.200.000
  * ID 2 - Fecha: 2025-03-15 - Monto: $800.000

====================================================================
  [ÉXITO] TODAS LAS PRUEBAS DEL EXAMEN 1 COMPLETADAS SATISFACTORIAMENTE
====================================================================
```

---

## 🎯 Conclusión de Cumplimiento

* **Punto 1:** Consultas generales, por ID y por cliente funcionando y manejando excepciones.
* **Punto 2:** `buscarPrestamosPorMonto(minimo, maximo)` con Stream API y validación de rangos no negativos.
* **Punto 3:** `buscarPrestamosPorEstado(estado)` con validación de estados enum del proyecto.
* **Punto 4:** Ordenamiento por monto (asc/desc) y por fecha mediante `Comparator` y `Stream.sorted()`.
* **Punto 5:** Arquitectura DAO y JDBC respetada con `PreparedStatement`.
* **Punto 6:** Integración completa y pruebas reproducibles.
