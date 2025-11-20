package com.ademarli.test.mapper;

import com.ademarli.test.dto.CreateTodoRequest;
import com.ademarli.test.dto.TodoDto;
import com.ademarli.test.dto.UpdateTodoRequest;
import com.ademarli.test.model.Todo;

public class TodoMapper {

    public static TodoDto toDto(Todo t) {
        if (t == null) return null;
        return new TodoDto(t.getId(), t.getTitle(), t.getDescription());
    }

    public static Todo toEntity(CreateTodoRequest r) {
        if (r == null) return null;
        Todo t = new Todo();
        t.setTitle(r.getTitle());
        t.setDescription(r.getDescription());
        return t;
    }

    public static void updateEntityFromRequest(UpdateTodoRequest r, Todo t) {
        if (r == null || t == null) return;
        if (r.getTitle() != null) t.setTitle(r.getTitle());
        if (r.getDescription() != null) t.setDescription(r.getDescription());

    }
}

