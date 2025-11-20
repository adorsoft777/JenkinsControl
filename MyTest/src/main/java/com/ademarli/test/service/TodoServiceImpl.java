package com.ademarli.test.service;

import com.ademarli.test.dto.CreateTodoRequest;
import com.ademarli.test.dto.TodoDto;
import com.ademarli.test.dto.UpdateTodoRequest;
import com.ademarli.test.exception.ResourceNotFoundException;
import com.ademarli.test.mapper.TodoMapper;
import com.ademarli.test.model.Todo;
import com.ademarli.test.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final EmailService emailService;

    public TodoServiceImpl(TodoRepository todoRepository, EmailService emailService) {
        this.todoRepository = todoRepository;
        this.emailService = emailService;
    }

    @Override
    public TodoDto create(CreateTodoRequest request) {
        Todo t = TodoMapper.toEntity(request);
        Todo saved = todoRepository.save(t);

        emailService.sendWelcomeEmail(saved);
        return TodoMapper.toDto(saved);
    }

    @Override
    public TodoDto getById(Long id) {
        Todo t = todoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Todo not found: " + id));
        return TodoMapper.toDto(t);
    }

    @Override
    public List<TodoDto> listAll() {
        return todoRepository.findAll().stream().map(TodoMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public TodoDto update(Long id, UpdateTodoRequest request) {
        Todo t = todoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Todo not found: " + id));
        TodoMapper.updateEntityFromRequest(request, t);
        Todo updated = todoRepository.save(t);
        return TodoMapper.toDto(updated);
    }

    @Override
    public boolean delete(Long id) {

        if (todoRepository.findById(id).isPresent()) {
            todoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

