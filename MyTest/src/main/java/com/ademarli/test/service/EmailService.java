package com.ademarli.test.service;


import com.ademarli.test.model.Todo;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void sendWelcomeEmail(Todo todo) {
        log.info("[EmailService] Welcome email to {} ({})", todo.getTitle(), todo.getDescription());
    }
}
