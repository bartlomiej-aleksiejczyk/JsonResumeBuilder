package com.example.jsonresumebuilderspring.cvbuildscheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CvBuildJobMessageSenderService {

    private final RabbitTemplate rabbitTemplate;
    private final CvBuildJobRepository cvBuildJobRepository;

    @Value("${job.exchange.name}")
    private String jobExchange;

    @Value("${job.routing.key}")
    private String jobRoutingKey;

    private void sendJob(String jobMessage) {
        rabbitTemplate.convertAndSend(jobExchange, jobRoutingKey, jobMessage);
    }

    public void publishJob(String jsonContent, String templateName) {
        CvBuildJob newJob = new CvBuildJob(jsonContent, templateName,JobStatus.PENDING);
        newJob = cvBuildJobRepository.save(newJob);

        String jobMessage = String.format("Job ID: %d, Content: %s, Template: %s", newJob.getId(), jsonContent, templateName);

        sendJob(jobMessage);
    }

}
