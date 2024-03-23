package com.example.jsonresumebuilderspring.cvbuildscheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CvBuildJobService {

    private final RabbitTemplate rabbitTemplate;
    private final CvBuildJobRepository cvBuildJobRepository;
    private final ObjectMapper objectMapper;

    @Value("${job.exchange.name}")
    private String jobExchange;

    @Value("${job.routing.key}")
    private String jobRoutingKey;

    private void sendJob(String jobMessage) {
        rabbitTemplate.convertAndSend(jobExchange, jobRoutingKey, jobMessage);
    }

    public void publishJob(CvBuildJobDTO cvBuildJobDTO) {

        CvBuildJob newJob = new CvBuildJob(cvBuildJobDTO.getJsonContent(), cvBuildJobDTO.getTemplateName(), JobStatus.PENDING);
        newJob = cvBuildJobRepository.save(newJob);

        //TODO: Reafactor this to spring-boot  --> Celery Bridge
        Map<String, Object> args = Map.of(
                "id", newJob.getId(),
                "content", cvBuildJobDTO.getJsonContent(),
                "template_name", cvBuildJobDTO.getTemplateName()
        );

        Map<String, Object> task = Map.of(
                "id", String.valueOf(newJob.getId()),
                "task", "tasks.log_message",
                "args", Collections.singletonList(args),
                "kwargs", Collections.emptyMap(),
                "retries", 0,
                "eta", null
        );

        try {
            String jobMessage = objectMapper.writeValueAsString(task);
            sendJob(jobMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing job message", e);
        }
    }

}
