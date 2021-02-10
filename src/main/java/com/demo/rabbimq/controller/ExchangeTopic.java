package com.demo.rabbimq.controller;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 例子：使用 topic 类型的交换机，将发送消息给与 routing_key 匹配的队列。
 * <p>
 * 类似路由模式，支的模糊匹配，按规则转发消息更加灵活。
 * 符号 “#” 匹配一个或多个词，符号 “*” 匹配不多不少一个词。
 */
@Configuration
@RestController
@RequestMapping("/")
public class ExchangeTopic {

    @Resource
    private AmqpTemplate template;

    /**
     * 声明队列
     */
    @Bean
    Queue testExchangeTopicQueue() {
        return new Queue("test-exchange-topic-queue", false, false, true);
    }

    /**
     * 声明交换机
     */
    @Bean
    TopicExchange testExchangeTopic() {
        return new TopicExchange("test-exchange-topic");
    }

    /**
     * 将队列与交换机进行绑定，设置消息路由条件
     */
    @Bean
    Binding topicBinding(Queue testExchangeTopicQueue, TopicExchange testExchangeTopic) {
        return BindingBuilder.bind(testExchangeTopicQueue).to(testExchangeTopic).with("org.cord.#");
    }

    /**
     * 声明监听频道
     */
    @RabbitListener(queues = "test-exchange-topic-queue")
    public void processMessage(String msg) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.format("%s test-exchange-topic-queue: %s \n", dateFormat.format(new Date()), msg);
    }

    /**
     * 发送消息
     */
    @GetMapping("test/exchangeTopic")
    public void sendMsg() {
        template.convertAndSend("test-exchange-topic", "org.cord.test", "Hello topic exchange.");
    }
}
