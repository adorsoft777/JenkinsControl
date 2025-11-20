package com.ademarli.test.controller;

import com.ademarli.test.dto.CreateTodoRequest;
import com.ademarli.test.dto.TodoDto;
import com.ademarli.test.dto.UpdateTodoRequest;
import com.ademarli.test.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public ResponseEntity<TodoDto> create(@Valid @RequestBody CreateTodoRequest request) {
        TodoDto created = todoService.create(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDto> getById(@PathVariable Long id) {
        TodoDto dto = todoService.getById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<TodoDto>> listAll() {
        return ResponseEntity.ok(todoService.listAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDto> update(@PathVariable("id") Long id, @RequestBody UpdateTodoRequest request) {
        TodoDto updated = todoService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = todoService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

