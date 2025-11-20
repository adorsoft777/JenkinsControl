package com.ademarli.test.dto;

public class UpdateTodoRequest {
    private String title;
    private String description;


    public UpdateTodoRequest() {
    }

    public UpdateTodoRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public UpdateTodoRequest(Long id, String eski, String eski1) {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

