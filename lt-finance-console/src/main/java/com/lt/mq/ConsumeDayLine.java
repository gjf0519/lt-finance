package com.lt.mq;

import com.alibaba.fastjson.JSON;
import com.lt.service.ReceiveService;
import com.lt.utils.Constants;
import com.lt.utils.TushareUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
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
public class ConsumeDayLine {

    @Value("${rocketmq.name-server}")
    private String nameSrvAddr;
    @Value("${rocketmq.comsumer.day-line}")
    private String consumerGroup;
    @Autowired
    private ReceiveService receiveService;
    private DefaultMQPushConsumer consumerDayLine;
    private String topicName = TushareUtil.TUSHARE_DAYLINE_TOPIC;

    @PostConstruct
    public void init() throws Exception {
        log.info("开始启动日K数据消费者服务...");
        ConsumeInit.ConsumerParam consumerParam = ConsumeInit.ConsumerParam.builder()
                .consumerGroup(consumerGroup).namesrvAddr(nameSrvAddr).topicName(topicName)
                .messageListener(new ConsumeDayLine.ConcurrentListener(receiveService)).build();
        consumerDayLine = ConsumeInit.concurrentConsumer(consumerParam);
        consumerDayLine.start();
        log.info("日K数据消息消费者服务启动成功.");
    }

    @PreDestroy
    public void destroy() {
        log.info("开始关闭日K数据消息消费者服务...");
        consumerDayLine.shutdown();
        log.info("日K数据消息消费者服务已关闭.");
    }

    private static class ConcurrentListener implements MessageListenerConcurrently {
        private ReceiveService receiveService;
        public ConcurrentListener(ReceiveService receiveService){
            this.receiveService = receiveService;
        }
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                        ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            for (MessageExt ext : msgs) {
                try {
                    String record = new String(ext.getBody(), RemotingHelper.DEFAULT_CHARSET);
                    Map<String,String> map = JSON.parseObject(record, Map.class);
                    receiveService.receiveDayLine(map);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("DAY数据并发开始消费");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    private static class OrderListener implements MessageListenerOrderly {
        private ReceiveService receiveService;
        public OrderListener(ReceiveService receiveService){
            this.receiveService = receiveService;
        }
        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
            for (MessageExt ext : msgs) {
                try {
                    String record = new String(ext.getBody(), RemotingHelper.DEFAULT_CHARSET);
                    Map<String,String> map = JSON.parseObject(record, Map.class);
                    receiveService.receiveDayLine(map);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("DAY数据顺序开始消费");
            return ConsumeOrderlyStatus.SUCCESS;
        }
    }
}
