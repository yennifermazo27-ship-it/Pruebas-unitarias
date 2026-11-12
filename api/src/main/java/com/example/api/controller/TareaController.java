package com.example.api.controller;

import com.example.api.model.Tarea;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/tareas")
public class TareaController {

    private final List<Tarea> tareas = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    // Listar todas
    @GetMapping
    public List<Tarea> listar() {
        return tareas;
    }

    // Obtener una por id
    @GetMapping("/{id}")
    public ResponseEntity<Tarea> obtener(@PathVariable Long id) {
        return tareas.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear
    @PostMapping
    public ResponseEntity<Tarea> crear(@RequestBody Tarea tarea) {
        tarea.setId(contador.getAndIncrement());
        tareas.add(tarea);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarea);
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Tarea> actualizar(@PathVariable Long id, @RequestBody Tarea datos) {
        for (Tarea t : tareas) {
            if (t.getId().equals(id)) {
                t.setTitulo(datos.getTitulo());
                t.setDescripcion(datos.getDescripcion());
                t.setCompletada(datos.isCompletada());
                return ResponseEntity.ok(t);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminada = tareas.removeIf(t -> t.getId().equals(id));
        return eliminada ? ResponseEntity.noContent().build()
                         : ResponseEntity.notFound().build();
    }
}