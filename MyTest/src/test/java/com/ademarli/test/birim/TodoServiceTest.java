package com.ademarli.test.birim;

import com.ademarli.test.dto.CreateTodoRequest;
import com.ademarli.test.dto.TodoDto;
import com.ademarli.test.model.Todo;
import com.ademarli.test.repository.TodoRepository;
import com.ademarli.test.service.EmailService;
import com.ademarli.test.service.TodoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    EmailService emailService;

    @InjectMocks
    private TodoServiceImpl todoService;

    @Test
    void create_shouldSaveAndReturnDto() {
        CreateTodoRequest req = new CreateTodoRequest("Test title", "desc");

        Todo saved = new Todo();
        saved.setId(1L);
        saved.setTitle(req.getTitle());
        saved.setDescription(req.getDescription());

        when(todoRepository.save(any(Todo.class))).thenReturn(saved);

        TodoDto dto = todoService.create(req);

        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
        verify(todoRepository).save(captor.capture());
        Todo persisted = captor.getValue();

        assertThat(persisted.getTitle()).isEqualTo(req.getTitle());
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo(req.getTitle());
    }

    @Test
    void getById_whenNotFound_shouldThrow() {
        when(todoRepository.findById(10L)).thenReturn(Optional.empty());

        try {
            todoService.getById(10L);
        } catch (RuntimeException ex) {
            assertThat(ex.getMessage()).contains("Todo not found");
        }

        verify(todoRepository).findById(10L);
    }
}

