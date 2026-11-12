package com.example.api.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TareaControllerIntegrationTest {

    private static final String RUTA = "/tareas";

    private static final String JSON_BASE = """
            {
              "titulo": "Estudiar Spring",
              "descripcion": "Preparar la evaluacion",
              "completada": false
            }
            """;

    private static final long ID_INEXISTENTE = 9_999_999L;

    @Autowired
    private MockMvc mockMvc;

    // Crea una tarea por HTTP y devuelve el id generado por el servidor
    private long crearTarea(String titulo, String descripcion, boolean completada) throws Exception {
        String json = """
                {
                  "titulo": "%s",
                  "descripcion": "%s",
                  "completada": %s
                }
                """.formatted(titulo, descripcion, completada);

        String respuesta = mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Number id = JsonPath.read(respuesta, "$.id");
        return id.longValue();
    }

    @Test
    void crearTarea_conJsonValido_deberiaRetornar201YElJsonCompleto() throws Exception {
        // Arrange: JSON valido con todos los campos del modelo
        // Act + Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_BASE))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Estudiar Spring"))
                .andExpect(jsonPath("$.descripcion").value("Preparar la evaluacion"))
                .andExpect(jsonPath("$.completada").value(false));
    }

    @Test
    void listarTareas_deberiaRetornar200YUnArregloJson() throws Exception {
        // Arrange: se crea una tarea para verificar que aparece en el arreglo
        long id = crearTarea("Tarea de listado", "Verificar arreglo", false);

        // Act + Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").isNotEmpty())
                .andExpect(jsonPath("$[?(@.id == " + id + ")].titulo").value("Tarea de listado"));
    }

    @Test
    void obtenerTareaPorId_deberiaRetornar200SiExisteY404SiNoExiste() throws Exception {
        // Arrange: id real devuelto por el POST
        long id = crearTarea("Tarea consultable", "Buscar por id", false);

        // Act + Assert: existente
        mockMvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value("Tarea consultable"))
                .andExpect(jsonPath("$.descripcion").value("Buscar por id"))
                .andExpect(jsonPath("$.completada").value(false));

        // Act + Assert: inexistente
        mockMvc.perform(get(RUTA + "/{id}", ID_INEXISTENTE))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarTareaPorId_deberiaRetornar200SiExisteY404SiNoExiste() throws Exception {
        // Arrange: tarea existente y JSON de actualizacion
        long id = crearTarea("Titulo anterior", "Descripcion anterior", false);
        String jsonActualizado = """
                {
                  "titulo": "Titulo nuevo",
                  "descripcion": "Descripcion nueva",
                  "completada": true
                }
                """;

        // Act + Assert: actualizacion exitosa
        mockMvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonActualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value("Titulo nuevo"))
                .andExpect(jsonPath("$.descripcion").value("Descripcion nueva"))
                .andExpect(jsonPath("$.completada").value(true));

        // Act + Assert: id inexistente
        mockMvc.perform(put(RUTA + "/{id}", ID_INEXISTENTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonActualizado))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarTareaPorId_deberiaRetornar204SiExisteY404SiNoExiste() throws Exception {
        // Arrange: tarea existente
        long id = crearTarea("Tarea eliminable", "Se eliminara", false);

        // Act + Assert: eliminacion exitosa
        mockMvc.perform(delete(RUTA + "/{id}", id))
                .andExpect(status().isNoContent());

        // Assert: ya no existe
        mockMvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isNotFound());

        // Act + Assert: id inexistente
        mockMvc.perform(delete(RUTA + "/{id}", ID_INEXISTENTE))
                .andExpect(status().isNotFound());
    }

    @Test
    void flujoCompleto_crearConsultarActualizarEliminar_deberiaFuncionar() throws Exception {
        // Arrange: crear la tarea con datos iniciales
        long id = crearTarea("Tarea de flujo", "Version inicial", false);

        // Act + Assert: consultar la tarea creada
        mockMvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Tarea de flujo"))
                .andExpect(jsonPath("$.descripcion").value("Version inicial"))
                .andExpect(jsonPath("$.completada").value(false));

        // Act + Assert: actualizar la tarea
        String jsonActualizado = """
                {
                  "titulo": "Tarea de flujo actualizada",
                  "descripcion": "Version final",
                  "completada": true
                }
                """;
        mockMvc.perform(put(RUTA + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonActualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value("Tarea de flujo actualizada"))
                .andExpect(jsonPath("$.descripcion").value("Version final"))
                .andExpect(jsonPath("$.completada").value(true));

        // Act + Assert: eliminar la tarea
        mockMvc.perform(delete(RUTA + "/{id}", id))
                .andExpect(status().isNoContent());

        // Assert: verificar que ya no existe
        mockMvc.perform(get(RUTA + "/{id}", id))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(RUTA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]", hasSize(0)));
    }
}
