package com.demo.rabbimq.controller;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
 * 例子：使用自定义（header）类型的交换机，向符合条件的队列发送消息。
 */
@RestController
@RequestMapping("/")
public class ExchangeHeader {

    @Resource
    private AmqpTemplate template;

    /**
     * 声明队列
     */
    @Bean
    Queue testExchangeHeaderQueue() {
        return new Queue("test-exchange-header-queue", false, true, true);
    }

    /**
     * 声明交换机
     */
    @Bean
    HeadersExchange testExchangeHeader() {
        return new HeadersExchange("test-exchange-header");
    }

    /**
     * 将队列与交换机进行绑定，设置队列接收消息的条件。
     */
    @Bean
    Binding headersBinding(Queue testExchangeHeaderQueue, HeadersExchange testExchangeHeader) {
        Map<String, Object> map = new HashMap<>();
        map.put("First", "A");
        map.put("Fourth", "D");
        return BindingBuilder.bind(testExchangeHeaderQueue).to(testExchangeHeader).whereAny(map).match();
        // whereAny：表示部分匹配
        // whereAll：表示全部匹配
        // return BindingBuilder.bind(queue).to(headersExchange).whereAll(map).match();
    }

    /**
     * 声明监听频道
     */
    @RabbitListener(queues = "test-exchange-header-queue")
    public void processMessage1(String msg) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.format("%s test-exchange-header-queue: %s \n", dateFormat.format(new Date()), msg);
    }

    /**
     * 发送消息
     */
    @GetMapping("test/exchangeHeader1")
    public void sendMsg1() {
        Map<String, Object> map = new HashMap<>();
        map.put("First", "A");
        template.convertAndSend("test-exchange-header", null, "hello fanout exchange 1.", message->{
            message.getMessageProperties().getHeaders().putAll(map);
            return message;
        });
    }
    @GetMapping("test/exchangeHeader2")
    public void sendMsg2() {
        Map<String, Object> map = new HashMap<>();
        map.put("Fourth", "D");
        template.convertAndSend("test-exchange-header", null, "hello fanout exchange 2.", message->{
            message.getMessageProperties().getHeaders().putAll(map);
            return message;
        });
    }
}
