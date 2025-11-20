package com.ademarli.test.entegrasyon;


import com.ademarli.test.dto.CreateTodoRequest;
import com.ademarli.test.dto.TodoDto;
import com.ademarli.test.dto.UpdateTodoRequest;
import com.ademarli.test.model.Todo;
import com.ademarli.test.repository.TodoRepository;
import com.ademarli.test.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TodoControllerIT extends BaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TodoRepository todoRepository;

    @MockitoBean
    EmailService emailService;

    @BeforeEach
    void clean() {
        todoRepository.deleteAll();
    }


    @Test
    void testCreateTodo() throws Exception {

        CreateTodoRequest request=new CreateTodoRequest("title","description");

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber());


        assertThat(todoRepository.findByTitle("title")).isPresent();
        verify(emailService,times(1)).sendWelcomeEmail(any(Todo.class));

    }

    @Test
    void testCreateTodo_ValidationError() throws Exception {

        CreateTodoRequest request=new CreateTodoRequest("","Null kontrolu");

        mockMvc.perform(post("/api/todos")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)

        )
                .andExpect(status().isBadRequest());

        assertThat(todoRepository.findAll()).isEmpty();
        verify(emailService,times(0)).sendWelcomeEmail(any(Todo.class));

    }

    @Test
    void UpdateTodoTest()throws Exception{

        CreateTodoRequest request= new CreateTodoRequest("title","description");

        String response=mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

        verify(emailService,times(1)).sendWelcomeEmail(any(Todo.class));

        System.out.println("-----------"+response+"---------------------------------");
        TodoDto dto=objectMapper.readValue(response,TodoDto.class);
        assertThat(todoRepository.findById(dto.getId())).isPresent();



        String updateRequestJson=objectMapper.writeValueAsString(new UpdateTodoRequest("Updated Title","Updated Description"));
        System.out.println("-----------"+updateRequestJson+"---------------------------------");
        String updatedJson = mockMvc.perform(put("/api/todos/" + dto.getId())
                        .content(updateRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andReturn()
                .getResponse()
                .getContentAsString();


        System.out.println("-----------"+updatedJson+"---------------------------------");
        Todo updatedFromDb = todoRepository.findById(dto.getId()).orElseThrow();
        assertThat(updatedFromDb.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedFromDb.getDescription()).isEqualTo("Updated Description");
    }
}
