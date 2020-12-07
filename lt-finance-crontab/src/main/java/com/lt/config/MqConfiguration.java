package com.lt.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gaijf
 * @description
 * @date 2020/5/18
 */
@Log4j2
@Configuration
public class MqConfiguration {

    @Value("${rocketmq.producer.groupName}")
    private String groupName;
    @Value("${rocketmq.producer.namesrvAddr}")
    private String namesrvAddr;

    /**
     * 创建普通消息发送者实例
     *
     * @return
     * @throws MQClientException
     */
    @Bean
    public DefaultMQProducer defaultProducer() throws MQClientException {
        log.info("defaultProducer 正在创建---------------------------------------");
        DefaultMQProducer producer = new DefaultMQProducer(groupName);

        producer.setNamesrvAddr(namesrvAddr);
        producer.setSendMsgTimeout(10000);
        producer.setVipChannelEnabled(false);
        producer.setRetryTimesWhenSendAsyncFailed(2);
        producer.start();
        log.info("rocketmq producer server开启成功---------------------------------.");
        return producer;
    }

    public static <T> void send(String topic,T msg,DefaultMQProducer defaultMQProducer){
        try {
            Message message = new Message(topic,
                    JSON.toJSONString(msg).getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 发送消息
            defaultMQProducer.send(message);
        }catch (Exception e){
            log.info("rocketmq producer 同步发送消息异常:{}",e);
        }
    }

    public static <T> void asynchSend(String topic,T msg,DefaultMQProducer defaultMQProducer){
        try {
            Message message = new Message(topic,
                    JSON.toJSONString(msg).getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 发送消息
            defaultMQProducer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                }
                @Override
                public void onException(Throwable e) {
                    log.info("rocketmq producer 异步发送消息异常:{}",e);
                }
            });
        }catch (Exception e){
            log.info("rocketmq producer 异步发送消息异常:{}",e);
        }
    }
}
