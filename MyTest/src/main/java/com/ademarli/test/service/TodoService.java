package com.ademarli.test.service;

import com.ademarli.test.dto.CreateTodoRequest;
import com.ademarli.test.dto.TodoDto;
import com.ademarli.test.dto.UpdateTodoRequest;

import java.util.List;

public interface TodoService {
    TodoDto create(CreateTodoRequest request);

    TodoDto getById(Long id);

    List<TodoDto> listAll();

    TodoDto update(Long id, UpdateTodoRequest request);

    boolean delete(Long id);
}

