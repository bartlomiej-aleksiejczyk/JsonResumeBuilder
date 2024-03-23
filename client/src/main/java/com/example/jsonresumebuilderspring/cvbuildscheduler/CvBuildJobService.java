package com.example.jsonresumebuilderspring.cvbuildscheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CvBuildJobService {

    private final RabbitTemplate rabbitTemplate;
    private final CvBuildJobRepository cvBuildJobRepository;

    @Value("${job.exchange.name}")
    private String jobExchange;

    @Value("${job.routing.key}")
    private String jobRoutingKey;

    private void sendJob(String jobMessage) {
        rabbitTemplate.convertAndSend(jobExchange, jobRoutingKey, jobMessage);
    }

lic void publishJob(CvBuildJobDTO cvBuildJobDTO) {

    CvBuildJob newJob = new CvBuildJob(cvBuildJobDTO.getJsonContent(), cvBuildJobDTO.getTemplateName(), JobStatus.PENDING);
    newJob = cvBuildJobRepository.save(newJob);

    JSONObject task = new JSONObject();
    JSONObject args = new JSONObject();
    args.put("id", newJob.getId());
    args.put("content", cvBuildJobDTO.getJsonContent());
    args.put("template_name", cvBuildJobDTO.getTemplateName());
    
    JSONArray argsArray = new JSONArray();
    argsArray.put(args);
    
    task.put("id", String.valueOf(newJob.getId()));
    task.put("task", "tasks.log_message"); 
    task.put("args", argsArray);
    task.put("kwargs", new JSONObject());
    task.put("retries", 0);
    task.put("eta", null);

    String jobMessage = task.toString();
    sendJob(jobMessage);
}

}
