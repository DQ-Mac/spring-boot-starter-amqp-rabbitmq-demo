package com.demo.rabbimq.controller;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 例子：设置消息的过期时间。
 *
 * 可以在声明队列时和发送消息时设置消息过期时间。
 * 本例子不用设置消息监听，在 RabbitMQ 控制台观察，消息进入队列 5 秒后将自动删除。
 */
@RestController
@RequestMapping("/")
public class MessageTtl {

    @Resource
    private AmqpTemplate template;

    /**
     * 声明队列
     *
     * 第一种方法：在声明队列时，对此队列中的所有消息设置消息过期时间。
     * 通过 x-message-ttl 参数设置过期时间为 20 秒。
     */
    @Bean
    Queue testMsgTtlQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-message-ttl", 1000 * 20);
        return new Queue("test-msg-ttl-queue", false, false, true, arguments);
    }

    /**
     * 发送消息
     */
    @GetMapping("/test/msgTtl1")
    public void sendMsg() {
        template.convertAndSend("test-msg-ttl-queue", "Hello");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.format("%s test-msg-ttl-queue: %s \n", dateFormat.format(new Date()), "消息已发送，将在 20 秒后过期。");
    }

    /**
     * 发送消息
     *
     * 第二种方法，在发送消息时，对本条消息设置消息过期时间。
     * 通过 messageProperties 的 setExpiration 方法设置过期时间为 15 秒。
     */
    @GetMapping("/test/msgTtl2")
    public void sendMsg1() {
        Map<String, Object> arguments = new HashMap<>();
        template.convertAndSend("test-msg-ttl-queue", "Hello", message -> {
            MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setExpiration("15000");    // 设置这条消息的过期时间
            return message;
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.format("%s test-msg-ttl-queue: %s \n", dateFormat.format(new Date()), "消息已发送，将在 15 秒后过期。");
    }
}
