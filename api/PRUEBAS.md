# PRUEBAS - API REST de Tareas

## Descripción del proyecto

API REST desarrollada con Spring Boot que gestiona tareas en memoria. Permite listar, crear, consultar, actualizar y eliminar tareas mediante los endpoints `/tareas` y `/tareas/{id}`. Cada tarea tiene `id`, `titulo`, `descripcion` y `completada`.

> Nota: el proyecto real usa el paquete base `com.example.api` y la ruta `/tareas` (no `com.example.tareas` ni `/api/tareas`). Las pruebas se adaptaron a esa estructura.

## Tecnologías usadas

| Tecnología | Versión |
|---|---|
| Java (configuración del proyecto) | 17 |
| JDK usado en la ejecución | 21.0.8 |
| Spring Boot | 4.1.2 |
| Spring Framework / Spring Test | 7.0.10 |
| JUnit Jupiter | 6.0.3 |
| AssertJ | 3.27.7 |
| MockMvc | Incluido en Spring Test 7.0.10 |
| Maven Wrapper | Surefire 3.5.6 / Compiler 3.15.0 |

Dependencias de prueba declaradas en `pom.xml`:

- `spring-boot-starter-test` (scope `test`)
- `spring-boot-starter-webmvc-test` (scope `test`, necesaria en Spring Boot 4 para MockMvc)

## Estructura de las carpetas y archivos de prueba

```text
api/
├── pom.xml
├── PRUEBAS.md
└── src/
    ├── main/
    │   └── java/com/example/api/
    │       ├── ApiApplication.java
    │       ├── controller/TareaController.java
    │       └── model/Tarea.java
    └── test/
        └── java/com/example/api/
            ├── ApiApplicationTests.java
            ├── controller/
            │   ├── TareaControllerTest.java
            │   └── TareaControllerIntegrationTest.java
            └── model/
                └── TareaTest.java
```

## Diferencia entre pruebas unitarias y de integración en este proyecto

**Pruebas unitarias** (`TareaControllerTest`, `TareaTest`): instancian directamente las clases con `new`, sin levantar el contexto de Spring. Verifican la lógica aislada del controlador y del modelo (asignación de ids, respuestas `ResponseEntity`, getters/setters). Son rápidas y no prueban el mapeo HTTP real.

**Pruebas de integración** (`TareaControllerIntegrationTest`): usan `@SpringBootTest` y `@AutoConfigureMockMvc` para levantar el contexto de Spring y realizar peticiones HTTP simuladas contra los endpoints reales (`/tareas`). Validan el mapeo de rutas, la serialización/deserialización JSON, los códigos de estado y el flujo completo entre capas.

## Tablas de pruebas

### TareaControllerTest.java (unitarias, 8 pruebas)

| Nombre de la prueba | Qué valida | Resultado esperado |
|---|---|---|
| `listar_cuandoNoHayTareas_deberiaRetornarListaVacia` | Listado con el controlador recién creado | Lista no nula y vacía |
| `crearTarea_deberiaAsignarIdAutoincrementalYRetornar201` | Creación de dos tareas y asignación consecutiva de ids | HTTP 201, ids 1 y 2, lista con 2 elementos |
| `obtenerPorIdExistente_deberiaRetornar200ConLaTarea` | Búsqueda por un id creado previamente | HTTP 200 con los datos correctos |
| `obtenerPorIdInexistente_deberiaRetornar404` | Búsqueda de un id no registrado | HTTP 404 y cuerpo nulo |
| `actualizarTareaExistente_deberiaModificarSusDatos` | Actualización de título, descripción y estado | HTTP 200 con los datos modificados |
| `actualizarTareaInexistente_deberiaRetornar404` | Actualización de un id no registrado | HTTP 404 y cuerpo nulo |
| `eliminarTareaExistente_deberiaRetornar204YDesaparecerDeLaLista` | Eliminación de una tarea existente | HTTP 204, lista vacía y posterior GET 404 |
| `eliminarTareaInexistente_deberiaRetornar404` | Eliminación de un id no registrado | HTTP 404 y lista sin cambios |

### TareaTest.java (modelo, 3 pruebas)

| Nombre de la prueba | Qué valida | Resultado esperado |
|---|---|---|
| `constructorVacio_deberiaCrearTareaConValoresPorDefecto` | Estado inicial del modelo | `id`, `titulo` y `descripcion` nulos; `completada` en `false` |
| `settersYGetters_deberianAsignarYRetornarLosValores` | Asignación y lectura de todos los campos | Todos los valores coinciden con los asignados |
| `setCompletada_deberiaCambiarElEstadoBooleano` | Cambio del estado booleano | El estado pasa de `false` a `true` |

### TareaControllerIntegrationTest.java (integración, 6 pruebas)

| Nombre de la prueba | Qué valida | Resultado esperado |
|---|---|---|
| `crearTarea_conJsonValido_deberiaRetornar201YElJsonCompleto` | `POST /tareas` con JSON válido | HTTP 201 y JSON con `id`, `titulo`, `descripcion` y `completada` |
| `listarTareas_deberiaRetornar200YUnArregloJson` | `GET /tareas` | HTTP 200 y arreglo JSON que contiene la tarea creada |
| `obtenerTareaPorId_deberiaRetornar200SiExisteY404SiNoExiste` | `GET /tareas/{id}` en ambos casos | HTTP 200 con la tarea correcta / HTTP 404 |
| `actualizarTareaPorId_deberiaRetornar200SiExisteY404SiNoExiste` | `PUT /tareas/{id}` en ambos casos | HTTP 200 con datos actualizados / HTTP 404 |
| `eliminarTareaPorId_deberiaRetornar204SiExisteY404SiNoExiste` | `DELETE /tareas/{id}` en ambos casos | HTTP 204 y posterior GET 404 / HTTP 404 |
| `flujoCompleto_crearConsultarActualizarEliminar_deberiaFuncionar` | Flujo crear → consultar → actualizar → eliminar | Cada paso con su código esperado y la tarea ya no existe al final |

### ApiApplicationTests.java (contexto, 1 prueba)

| Nombre de la prueba | Qué valida | Resultado esperado |
|---|---|---|
| `contextLoads` | Carga del contexto de Spring Boot | El contexto inicia sin errores |

## Cómo ejecutar las pruebas

Todas las pruebas (Windows):

```powershell
.\mvnw.cmd test
```

Todas las pruebas (Linux/macOS):

```bash
./mvnw test
```

Una clase específica:

```powershell
.\mvnw.cmd test -Dtest=TareaControllerTest
.\mvnw.cmd test -Dtest=TareaTest
.\mvnw.cmd test -Dtest=TareaControllerIntegrationTest
```

Un método específico:

```powershell
.\mvnw.cmd test -Dtest=TareaControllerTest#crearTarea_deberiaAsignarIdAutoincrementalYRetornar201
```

## Resultado real de la última ejecución

```text
Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

| Clase de prueba | Pruebas | Pasadas | Fallidas |
|---|---:|---:|---:|
| `TareaControllerTest` | 8 | 8 | 0 |
| `TareaTest` | 3 | 3 | 0 |
| `TareaControllerIntegrationTest` | 6 | 6 | 0 |
| `ApiApplicationTests` | 1 | 1 | 0 |
| **Total** | **18** | **18** | **0** |

## Posibles mejoras

- Agregar validaciones con `jakarta.validation` (`@NotBlank`, `@Size`) y manejar errores con `@RestControllerAdvice` para devolver respuestas 400 con detalles.
- Reemplazar el almacenamiento en memoria por una base de datos (JPA/H2 en pruebas) para persistencia real y pruebas con transacciones.
- Añadir pruebas de casos límite: JSON inválido, campos vacíos, ids negativos o nulos y cuerpos mal formados.
- Incorporar pruebas parametrizadas para reducir duplicación en los casos de éxito/error.
- Añadir cobertura de código con JaCoCo y un umbral mínimo en el build.
- Externalizar la ruta base y usar `@WebMvcTest` para pruebas de capa web más ligeras.
- Añadir documentación de la API con OpenAPI/Swagger.
