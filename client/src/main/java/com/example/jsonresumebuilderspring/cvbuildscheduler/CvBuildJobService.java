package com.example.jsonresumebuilderspring.cvbuildscheduler;

import com.example.jsonresumebuilderspring.cvbuildscheduler.exceptions.CelerySerializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CvBuildJobService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final CvBuildJobRepository cvBuildJobRepository;

    @Value("${QUEUE_CELERY_TASK_BUILD_CV}")
    private String celeryTaskName;

    @Value("${QUEUE_EXCHANGE}")
    private String jobExchange;

    @Value("${QUEUE_ROUTING_KEY}")
    private String jobRoutingKey;

    private void sendJob(Map<String, Object> task) throws CelerySerializationException {
        try {
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            String messageBody = objectMapper.writeValueAsString(task);
            Message message = new Message(messageBody.getBytes(), messageProperties);
            rabbitTemplate.send(jobExchange, jobRoutingKey, message);
        } catch (JsonProcessingException e) {
            throw new CelerySerializationException("Error serializing Celery job message", e);
        }
    }

    public void publishJob(CvBuildJobDTO cvBuildJobDTO) throws CelerySerializationException {
        CvBuildJob newJob = new CvBuildJob(
                cvBuildJobDTO.getJsonContent(),
                cvBuildJobDTO.getTemplateName(),
                JobStatus.PENDING);
        newJob = cvBuildJobRepository.save(newJob);
        Long newJobId = newJob.getId();

        Map<String, Object> task = new HashMap<>();
        Map<String, Object> args = new HashMap<>();

        args.put("id", newJobId);
        args.put("content", cvBuildJobDTO.getJsonContent());
        args.put("template_name", cvBuildJobDTO.getTemplateName());

        task.put("id", String.valueOf(newJob.getId()));
        task.put("task", celeryTaskName);
        task.put("args", new Object[] { args });
        task.put("kwargs", new HashMap<>());
        task.put("retries", 0);
        task.put("eta", null);

        sendJob(task);
    }
}
