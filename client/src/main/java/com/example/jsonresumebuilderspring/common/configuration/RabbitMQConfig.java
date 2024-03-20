package com.example.jsonresumebuilderspring.common.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${job.queue.name}")
    private String jobQueueName;

    @Value("${job.exchange.name}")
    private String jobExchangeName;

    @Value("${job.routing.key}")
    private String jobRoutingKey;

    @Bean
    Queue queue() {
        return new Queue(jobQueueName, true);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(jobExchangeName);
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(jobRoutingKey);
    }
}
