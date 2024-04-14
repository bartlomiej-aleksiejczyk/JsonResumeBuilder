package com.example.jsonresumebuilderworker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.Channel;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;

public class Worker {

    private static final Logger logger = LoggerFactory.getLogger(Worker.class);
    private static final String QUEUE_NAME = System.getenv("QUEUE_NAME");
    private static final String RABBITMQ_SERVER = System.getenv("CELERY_BROKER_URL");
    private static final String SPRING_SINGLE_LOGIN = System.getenv("SPRING_SINGLE_LOGIN");
    private static final String SPRING_SINGLE_PASSWORD = System.getenv("SPRING_SINGLE_PASSWORD");

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RABBITMQ_SERVER);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        logger.info(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            logger.info(" [x] Received '" + message + "'");
            try {
                processMessage(message);
            } catch (Exception e) {
                logger.error("Error processing message: ", e);
            }
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }

    private static void processMessage(String message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> msgMap = mapper.readValue(message, HashMap.class);

        String message_id = (String) msgMap.get("id");
        String url = "http://springboot-server:8080/" + QUEUE_NAME + "/api/v1/cv-build-job/" + message_id + "/status";

        HttpClient httpClient = HttpClients.createDefault();
        HttpPatch patch = new HttpPatch(url);
        StringEntity entity = new StringEntity("{\"status\": \"COMPLETED\"}");
        patch.setEntity(entity);
        patch.setHeader("Content-type", "application/json");

        HttpResponse response = httpClient.execute(patch);
        String responseBody = EntityUtils.toString(response.getEntity());
        logger.info("Response status: " + response.getStatusLine().getStatusCode() + " " + responseBody);
    }
}

