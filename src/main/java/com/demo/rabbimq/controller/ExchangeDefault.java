package com.demo.rabbimq.controller;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 例子：直接向队列发送消息（使用默认的交换机）。
 * <p>
 * 在发送消息时，如果不指定交换机，将会使用默认的交换机；默认的交换机为 direct 类型。
 * 当交换机为 direct 类型时，routing_key 为需要收接消息的队列名称。
 */
@RestController
@RequestMapping("/")
public class ExchangeDefault {

    @Resource
    private AmqpTemplate template;

    /**
     * 声明队列
     */
    @Bean
    public Queue testExchangeDefaultQueue() {
        // name：队列名称
        // durable：是否持久化。
        // exclusive：是否排外。
        // autoDelete：是否自动删除。
        return new Queue("test-exchange-default-queue", false, false, true);
    }

    /**
     * 声明监听频道
     * <p>
     * 用来接收指定队列中的消息。
     */
    @RabbitListener(queues = "test-exchange-default-queue")
    public void processMessage(String msg) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.format("%s test-default-exchange-queue: %s \n", dateFormat.format(new Date()), msg);
    }

    /**
     * 发送消息
     * <p>
     * 将消息发送给指定的队列。
     */
    @GetMapping("test/exchangeDefault")
    public void sendMsg() {
        template.convertAndSend("test-exchange-default-queue", "Hello default exchange.");
    }
}
