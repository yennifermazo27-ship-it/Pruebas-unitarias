package com.example.api.controller;

import com.example.api.model.Tarea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TareaControllerTest {

    private TareaController controller;

    @BeforeEach
    void setUp() {
        // Se instancia el controlador directamente, sin levantar contexto de Spring
        controller = new TareaController();
    }

    @Test
    void listar_cuandoNoHayTareas_deberiaRetornarListaVacia() {
        // Arrange: controlador recien creado sin tareas
        // Act
        List<Tarea> resultado = controller.listar();

        // Assert
        assertThat(resultado).isNotNull().isEmpty();
    }

    @Test
    void crearTarea_deberiaAsignarIdAutoincrementalYRetornar201() {
        // Arrange: dos tareas nuevas
        Tarea primera = new Tarea();
        primera.setTitulo("Primera tarea");
        primera.setDescripcion("Descripcion de la primera");
        primera.setCompletada(false);

        Tarea segunda = new Tarea();
        segunda.setTitulo("Segunda tarea");
        segunda.setDescripcion("Descripcion de la segunda");
        segunda.setCompletada(true);

        // Act
        ResponseEntity<Tarea> respuesta1 = controller.crear(primera);
        ResponseEntity<Tarea> respuesta2 = controller.crear(segunda);

        // Assert
        assertThat(respuesta1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta1.getBody()).isSameAs(primera);
        assertThat(primera.getId()).isEqualTo(1L);

        assertThat(respuesta2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(segunda.getId()).isEqualTo(2L);
        assertThat(controller.listar()).hasSize(2);
    }

    @Test
    void obtenerPorIdExistente_deberiaRetornar200ConLaTarea() {
        // Arrange: se crea una tarea y se obtiene su id
        Tarea tarea = new Tarea();
        tarea.setTitulo("Estudiar JUnit");
        tarea.setDescripcion("Practicar pruebas unitarias");
        tarea.setCompletada(false);
        Long id = controller.crear(tarea).getBody().getId();

        // Act
        ResponseEntity<Tarea> respuesta = controller.obtener(id);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getId()).isEqualTo(id);
        assertThat(respuesta.getBody().getTitulo()).isEqualTo("Estudiar JUnit");
        assertThat(respuesta.getBody().getDescripcion()).isEqualTo("Practicar pruebas unitarias");
        assertThat(respuesta.getBody().isCompletada()).isFalse();
    }

    @Test
    void obtenerPorIdInexistente_deberiaRetornar404() {
        // Arrange: id que no existe en el controlador
        // Act
        ResponseEntity<Tarea> respuesta = controller.obtener(999L);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(respuesta.getBody()).isNull();
    }

    @Test
    void actualizarTareaExistente_deberiaModificarSusDatos() {
        // Arrange: tarea creada y datos nuevos
        Tarea original = new Tarea();
        original.setTitulo("Titulo original");
        original.setDescripcion("Descripcion original");
        original.setCompletada(false);
        Long id = controller.crear(original).getBody().getId();

        Tarea datosNuevos = new Tarea();
        datosNuevos.setTitulo("Titulo actualizado");
        datosNuevos.setDescripcion("Descripcion actualizada");
        datosNuevos.setCompletada(true);

        // Act
        ResponseEntity<Tarea> respuesta = controller.actualizar(id, datosNuevos);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getId()).isEqualTo(id);
        assertThat(respuesta.getBody().getTitulo()).isEqualTo("Titulo actualizado");
        assertThat(respuesta.getBody().getDescripcion()).isEqualTo("Descripcion actualizada");
        assertThat(respuesta.getBody().isCompletada()).isTrue();
        assertThat(controller.obtener(id).getBody().getTitulo()).isEqualTo("Titulo actualizado");
    }

    @Test
    void actualizarTareaInexistente_deberiaRetornar404() {
        // Arrange: datos para actualizar un id que no existe
        Tarea datosNuevos = new Tarea();
        datosNuevos.setTitulo("No existe");
        datosNuevos.setDescripcion("Sin tarea");
        datosNuevos.setCompletada(true);

        // Act
        ResponseEntity<Tarea> respuesta = controller.actualizar(999L, datosNuevos);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(respuesta.getBody()).isNull();
    }

    @Test
    void eliminarTareaExistente_deberiaRetornar204YDesaparecerDeLaLista() {
        // Arrange: tarea creada en el controlador
        Tarea tarea = new Tarea();
        tarea.setTitulo("Tarea a eliminar");
        Long id = controller.crear(tarea).getBody().getId();

        // Act
        ResponseEntity<Void> respuesta = controller.eliminar(id);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(respuesta.getBody()).isNull();
        assertThat(controller.listar()).isEmpty();
        assertThat(controller.obtener(id).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void eliminarTareaInexistente_deberiaRetornar404() {
        // Act: se intenta eliminar un id que no existe
        ResponseEntity<Void> respuesta = controller.eliminar(999L);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(controller.listar()).isEmpty();
    }
}
