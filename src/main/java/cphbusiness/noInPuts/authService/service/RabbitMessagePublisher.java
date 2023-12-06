package cphbusiness.noInPuts.authService.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// TODO: Documentation and create Interface
@Service
public class RabbitMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // TODO: USER DELETED EVENT
    /*public void createdUserEvent(String message) {
        rabbitTemplate.convertAndSend("", "userCreated", message);
    }*/
}
