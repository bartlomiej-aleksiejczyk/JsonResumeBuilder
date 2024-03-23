package com.example.jsonresumebuilderspring.cvbuildscheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
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
        String content = cvBuildJobDTO.getJsonContent() == null ? "" : cvBuildJobDTO.getJsonContent();
        String templateName = cvBuildJobDTO.getTemplateName() == null ? "" : cvBuildJobDTO.getTemplateName();

        CvBuildJob newJob = new CvBuildJob(content, templateName, JobStatus.PENDING);
        newJob = cvBuildJobRepository.save(newJob);

        Long jobId = newJob.getId();
        if (jobId == null) {
            throw new IllegalStateException("Job ID is null after saving to database.");
        }

        //TODO: Reafactor this to spring-boot  --> Celery Bridge
        Map<String, Object> args = new HashMap<>();
        args.put("id", jobId);
        args.put("content", content);
        args.put("template_name", templateName);

        Map<String, Object> task = new HashMap<>();
        task.put("id", String.valueOf(jobId));
        //TODO: extract log_message to env
        task.put("task", "tasks.log_message");
        task.put("args", Collections.singletonList(args));
        task.put("kwargs", Collections.emptyMap());
        task.put("retries", 0);
        task.put("eta", null);

        try {
            String jobMessage = objectMapper.writeValueAsString(task);
            sendJob(jobMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing job message", e);
        }
    }
}
