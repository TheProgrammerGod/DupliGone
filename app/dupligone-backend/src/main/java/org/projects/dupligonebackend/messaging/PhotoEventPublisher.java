package org.projects.dupligonebackend.messaging;

import org.projects.dupligonebackend.dto.PhotoUploadedPublishMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PhotoEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final String queueName;

    public PhotoEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.queue.photo-uploaded}") String queueName
    ){
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    public void publishPhotoUploaded(PhotoUploadedPublishMessage message){
        rabbitTemplate.convertAndSend(queueName, message);
    }

}
