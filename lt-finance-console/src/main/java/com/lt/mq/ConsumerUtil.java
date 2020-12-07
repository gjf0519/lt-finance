package com.lt.mq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;

public class ConsumerUtil {

    public static DefaultMQPushConsumer getConsumer(String consumerGroupName
                    , String nameServerAddr,
                     String topicName,
                     MessageListenerConcurrently messageListener) throws Exception {
        //创建一个消息消费者，并设置一个消息消费者组
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroupName);
        //指定 NameServer 地址
        consumer.setNamesrvAddr(nameServerAddr);
        consumer.setPullBatchSize(128);
        //重试次数、默认16次
        consumer.setMaxReconsumeTimes(2);
        // 批量消费,每次拉取128条
        consumer.setConsumeMessageBatchMaxSize(128);
        //订阅指定 Topic 下的所有消息
        consumer.subscribe(topicName, "*");
        //注册消息监听器
        consumer.registerMessageListener(messageListener);
        //设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        //设置Consumer第一次启动按指定时间点位消费
        //consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
        //consumer.setConsumeTimestamp("20200612092501");
        return consumer;
    }
}
