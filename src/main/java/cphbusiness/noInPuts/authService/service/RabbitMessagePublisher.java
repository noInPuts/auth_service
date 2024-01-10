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


    public void sendEmail(String receiver, String subject, String message) {
        rabbitTemplate.convertAndSend("", "sendEmail", "{\"name\": \""+ receiver + "\", \"email\":\""+ receiver +"\", \"subject\":\""+ subject +"\", \"msg\":\""+ message +"\"}");
    }
}
