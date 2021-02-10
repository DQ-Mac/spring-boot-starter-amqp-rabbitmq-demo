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

/**
 * 例子：使用订阅（fanout）类型的交换机，向所有绑定队列发送消息。
 * <p>
 * 向 fanout 类型的交换机发送消息时，与此交换机绑定的所有队列都将接收到消息。
 * <p>
 * 本例子声明了一个 fanout 交换机，绑定了两个 queue 队列。当向 fanout 交换机发送消息时，两个队列都会接收到消息。
 */
@RestController
@RequestMapping("/")
public class ExchangeFanout {

    @Resource
    private AmqpTemplate template;

    /**
     * 声明队列
     */
    @Bean
    Queue testExchangeFanoutQueue1() {
        return new Queue("test-exchange-fanout-queue-1", false, true, true);
    }
    @Bean
    Queue testExchangeFanoutQueue2() {
        return new Queue("test-exchange-fanout-queue-2", false, true, true);
    }

    /**
     * 声明交换机
     */
    @Bean
    FanoutExchange testExchangeFanout() {
        return new FanoutExchange("test-exchange-fanout");
    }

    /**
     * 将队列与交换机进行绑定
     */
    @Bean
    Binding fanoutBinding1(Queue testExchangeFanoutQueue1, FanoutExchange testExchangeFanout) {
        return BindingBuilder.bind(testExchangeFanoutQueue1).to(testExchangeFanout);
    }
    @Bean
    Binding fanoutBinding2(Queue testExchangeFanoutQueue2, FanoutExchange testExchangeFanout) {
        return BindingBuilder.bind(testExchangeFanoutQueue2).to(testExchangeFanout);
    }

    /**
     * 声明监听频道
     */
    @RabbitListener(queues = "test-exchange-fanout-queue-1")
    public void processMessage1(String msg) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.format("%s test-exchange-fanout-queue-1: %s \n", dateFormat.format(new Date()), msg);
    }
    @RabbitListener(queues = "test-exchange-fanout-queue-2")
    public void processMessage2(String msg) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.format("%s test-exchange-fanout-queue-2: %s \n", dateFormat.format(new Date()), msg);
    }

    /**
     * 发送消息
     */
    @GetMapping("test/exchangeFanout")
    public void sendMsg() {
        template.convertAndSend("test-exchange-fanout", "", "Hello fanout exchange.");
    }
}
