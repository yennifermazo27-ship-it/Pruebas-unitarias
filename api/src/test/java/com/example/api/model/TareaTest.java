package com.example.api.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TareaTest {

    @Test
    void constructorVacio_deberiaCrearTareaConValoresPorDefecto() {
        // Act: se crea una tarea sin inicializar sus campos
        Tarea tarea = new Tarea();

        // Assert
        assertThat(tarea.getId()).isNull();
        assertThat(tarea.getTitulo()).isNull();
        assertThat(tarea.getDescripcion()).isNull();
        assertThat(tarea.isCompletada()).isFalse();
    }

    @Test
    void settersYGetters_deberianAsignarYRetornarLosValores() {
        // Arrange: valores de prueba
        Tarea tarea = new Tarea();
        Long id = 10L;
        String titulo = "Aprender Spring Boot";
        String descripcion = "Crear pruebas de integracion";
        boolean completada = true;

        // Act
        tarea.setId(id);
        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        tarea.setCompletada(completada);

        // Assert
        assertThat(tarea.getId()).isEqualTo(id);
        assertThat(tarea.getTitulo()).isEqualTo(titulo);
        assertThat(tarea.getDescripcion()).isEqualTo(descripcion);
        assertThat(tarea.isCompletada()).isTrue();
    }

    @Test
    void setCompletada_deberiaCambiarElEstadoBooleano() {
        // Arrange: tarea inicialmente no completada
        Tarea tarea = new Tarea();
        assertThat(tarea.isCompletada()).isFalse();

        // Act
        tarea.setCompletada(true);

        // Assert
        assertThat(tarea.isCompletada()).isTrue();
    }
}
