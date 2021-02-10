package com.demo.rabbimq.controller;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 例子：声明监听频道，同时声明队列。
 */
@RestController
@RequestMapping("/")
public class QueuesToDeclare {

    @Resource
    private AmqpTemplate template;

    /**
     * 声明监听频道，同时声明队列
     */
    @RabbitListener(queuesToDeclare = @Queue(
            name = "test-queues-to-declare-queue",
            durable = "false",
            exclusive = "false",
            autoDelete = "true")
    )
    public void processMessage(String msg) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.format("%s test-queues-to-declare-queue: %s \n", dateFormat.format(new Date()), msg);
    }

    /**
     * 发送消息
     */
    @GetMapping("test/queuesToDeclare")
    public void sendMsg() {
        template.convertAndSend("test-queues-to-declare-queue", "Hello queues to declare.");
    }
}
