package com.lt.mq;

import com.alibaba.fastjson.JSON;
import com.lt.service.ReceiveService;
import com.lt.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author gaijf
 * @description 日K消费者
 * @date 2020/12/3
 */
@Slf4j
@Component
public class DayLineConsumer {

    @Value("${rocketmq.name-server}")
    private String nameServerAddr;
    private String topicName = Constants.TUSHARE_DAYLINE_TOPIC;
    private String consumerGroupName = "DAYLINE-CONSUMER-GROUP-WORK";
    private DefaultMQPushConsumer consumer;
    @Autowired
    private ReceiveService receiveService;

    @PostConstruct
    public void init() throws Exception {
        log.info("开始启动日K数据消费者服务...");
        consumer = ConsumerUtil.getConsumer(consumerGroupName,
                nameServerAddr,topicName,new Listener(receiveService));
        consumer.start();
        log.info("日K数据消息消费者服务启动成功.");
    }

    @PreDestroy
    public void destroy() {
        log.info("开始关闭日K数据消息消费者服务...");
        consumer.shutdown();
        log.info("日K数据消息消费者服务已关闭.");
    }

    private static class Listener implements MessageListenerConcurrently {
        private ReceiveService receiveService;
        public Listener(ReceiveService receiveService){
            this.receiveService = receiveService;
        }
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
                                                        ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            for (MessageExt ext : list) {
                try {
                    String record = new String(ext.getBody(), RemotingHelper.DEFAULT_CHARSET);
                    Map map = JSON.parseObject(record, Map.class);
                    receiveService.receiveDayLine(map);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println("日K数据开始消费");
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}
