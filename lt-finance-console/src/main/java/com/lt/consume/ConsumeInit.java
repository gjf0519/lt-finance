package com.lt.consume;

import lombok.Builder;
import lombok.Data;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;

public class ConsumeInit {

    public static DefaultMQPushConsumer initConsumerBase(){
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setPullBatchSize(128);
        //重试次数、默认16次
        consumer.setMaxReconsumeTimes(2);
        //设置Consumer第一次启动按指定时间点位消费
        //consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
        //consumer.setConsumeTimestamp("20200612092501");
        // 批量消费,每次拉取128条
        consumer.setConsumeMessageBatchMaxSize(128);
        //设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        return consumer;
    }

    public static DefaultMQPushConsumer orderConsumer(ConsumeInit.ConsumerParam param) throws Exception {
        DefaultMQPushConsumer consumer = initConsumerBase();
        //指定 NameServer 地址
        consumer.setNamesrvAddr(param.namesrvAddr);
        //设置一个消息消费者组
        consumer.setConsumerGroup(param.consumerGroup);
        //订阅指定 Topic 下的所有消息
        consumer.subscribe(param.topicName, "*");
        //注册消息监听器
        MessageListenerOrderly listener = (MessageListenerOrderly) param.messageListener;
        consumer.registerMessageListener(listener);
        return consumer;
    }

    public static DefaultMQPushConsumer concurrentConsumer(ConsumeInit.ConsumerParam param) throws Exception {
        DefaultMQPushConsumer consumer = initConsumerBase();
        //指定 NameServer 地址
        consumer.setNamesrvAddr(param.namesrvAddr);
        //设置一个消息消费者组
        consumer.setConsumerGroup(param.consumerGroup);
        //订阅指定 Topic 下的所有消息
        consumer.subscribe(param.topicName, "*");
        //注册消息监听器
        MessageListenerConcurrently listener = (MessageListenerConcurrently) param.messageListener;
        consumer.registerMessageListener(listener);
        return consumer;
    }

    @Data
    @Builder
    public static class ConsumerParam{
        private String namesrvAddr;
        private String consumerGroup;
        private String topicName;
        private MessageListener messageListener;
    }
}
