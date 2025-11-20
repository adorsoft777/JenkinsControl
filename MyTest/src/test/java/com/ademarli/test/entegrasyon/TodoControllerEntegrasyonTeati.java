package com.ademarli.test.entegrasyon;


import com.ademarli.test.dto.CreateTodoRequest;
import com.ademarli.test.dto.TodoDto;
import com.ademarli.test.dto.UpdateTodoRequest;
import com.ademarli.test.exception.ResourceNotFoundException;
import com.ademarli.test.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class TodoControllerEntegrasyonTeati {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    TodoService todoService;


    @Test
    void CreateandGetTodo() throws Exception {

        CreateTodoRequest request = new CreateTodoRequest("Todos title", "Todo desc");
        TodoDto created = new TodoDto(1L, "Todos title", "Todo desc");

        when(todoService.create(any(CreateTodoRequest.class))).thenReturn(created);
        when(todoService.getById(1L)).thenReturn(created);

        ResultActions resultActions = mockMvc.perform(post("/api/todos")
                .contentType("application/json")
                .content(mapper.writeValueAsString(request)));

        resultActions.andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("RESPONSE BODY = " + responseBody); // debug için

                    TodoDto responseTodo = mapper.readValue(responseBody, TodoDto.class);
                    assert responseTodo.getId().equals(1L);
                    assert responseTodo.getTitle().equals("Todos title");
                });

        ResultActions resultActions1 = mockMvc.perform(get("/api/todos/1"));
        resultActions1.andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody=result.getResponse().getContentAsString();
                    System.out.println("RESPONSE BODY = " + responseBody); // debug için

                    TodoDto responseTodo=mapper.readValue(responseBody,TodoDto.class);
                    assert responseTodo.getId().equals(1L);
                    assert responseTodo.getTitle().equals("Todos title");

                });
    }

    @Test
    void createAndGetAll() throws Exception {

        CreateTodoRequest request = new CreateTodoRequest("Deneme", "Deneme");
        TodoDto created = new TodoDto(1L, "Deneme", "Deneme");

        when(todoService.create(any(CreateTodoRequest.class))).thenReturn(created);
        when(todoService.listAll()).thenReturn(List.of(created));

        ResultActions resultActions = mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));

        resultActions.andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("RESPONSE BODY : " + responseBody);

                    TodoDto dto = mapper.readValue(responseBody, TodoDto.class);
                    assert dto.getId().equals(1L);
                });

        ResultActions resultActions1 = mockMvc.perform(
                get("/api/todos")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions1.andExpect(status().isOk())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    System.out.println("RESPONSE BODY: " + response);

                    TodoDto[] todos = mapper.readValue(response, TodoDto[].class);
                    assert todos.length == 1;
                    assert todos[0].getId().equals(1L);
                });
    }

    @Test
    void UpdateTodoAndGetById() throws Exception{

        //önce oluşturmak için gönder
        CreateTodoRequest create=new CreateTodoRequest("eski","eski");
        //Sonra güncellemek  için gönder
        TodoDto created=new TodoDto(1L,"eski","eski");

        //Güncel olarak bana geri gelecek
        TodoDto updated=new TodoDto(1L,"güncel","güncel");



        when(todoService.create(any(CreateTodoRequest.class))).thenReturn(created);
        when(todoService.update(any(Long.class),any(UpdateTodoRequest.class))).thenReturn(updated);


        String jsonBody=mapper.writeValueAsString(create);
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("JSON VERİSİ"+jsonBody);
        System.out.println("--------------------------------------------------------------------------");

        ResultActions resultActions=mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
        );
        resultActions.andExpect(status().isOk())
                .andExpect(result -> {
                    String body=result.getResponse().getContentAsString();
                    System.out.println("--------------------------------------------------------------------------");
                    System.out.println("CREATE SONUCU OLUŞAN JSON" +body);
                    System.out.println("--------------------------------------------------------------------------");

                    TodoDto dto=mapper.readValue(body,TodoDto.class);

                    assert dto.getId().equals(1L);
                }
        );

        String updatedJsonBody=mapper.writeValueAsString(create);
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("GÜNCELLEMEK İÇİN GÖNDERİLEN JSON"+updatedJsonBody);
        System.out.println("--------------------------------------------------------------------------");


        ResultActions resultActions1 = mockMvc.perform(put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJsonBody)
        );

        resultActions1.andExpect(status().isOk())
                .andExpectAll(result -> {

                    String body=result.getResponse().getContentAsString();

                    System.out.println("--------------------------------------------------------------------------");
                    System.out.println("UPDATED SOUNUCU OLUŞAN " +body);
                    System.out.println("--------------------------------------------------------------------------");

                    TodoDto dto=mapper.readValue(body,TodoDto.class);

                    assert dto.getTitle().equals(updated.getTitle());

                });

    }

    @Test
    void deleteTodoById() throws  Exception{

        TodoDto dto=new TodoDto(1L,"Silinicek","Silinicek");

        when(todoService.create(any(CreateTodoRequest.class))).thenReturn(dto);
        when(todoService.delete(any(Long.class))).thenReturn(true).thenReturn(false);

        String jsonBody=mapper.writeValueAsString(dto);



        String response=mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
        ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TodoDto created=mapper.readValue(response,TodoDto.class);




        mockMvc.perform(delete("/api/todos/"+created.getId())).andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/todos/"+created.getId())).andExpect(status().isNotFound());



    }

  /*
   Bu testleri yaptıktan sonra dönüş tipini değiştirdim Boolean yaptım
   @Test
    void deleteTodo_whenSuccess_shouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(todoService).delete(1L);

        ResultActions actions=mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());


    }

    @Test
    void deleteTodo_whenNotFound_shouldReturnNotFound() throws Exception {

        Long id=1L;

        Mockito.doThrow(new ResourceNotFoundException("Todo not found: " + id))
                .when(todoService).delete(id);

        mockMvc.perform(delete("/api/todos/1")).andExpect(status().isNotFound());

    }*/


}
