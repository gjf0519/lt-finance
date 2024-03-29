package com.lt.consume;

import com.alibaba.fastjson.JSON;
import com.lt.web.service.TushareService;
import com.lt.utils.TushareUtil;
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
 * @description 板块K消费者
 * @date 2020/12/3
 */
@Slf4j
@Component
public class PlateLineConsume {

    @Value("${rocketmq.name-server}")
    private String nameSrvAddr;
    @Value("${rocketmq.comsumer.plate-line}")
    private String consumerGroup;
    @Autowired
    private TushareService receiveService;
    private DefaultMQPushConsumer consumerPlateLine;
    private String topicName = TushareUtil.TUSHARE_PLATE_TOPIC;

    @PostConstruct
    public void init() throws Exception {
        log.info("开始启动日K数据消费者服务...");
        ConsumeInit.ConsumerParam consumerParam = ConsumeInit.ConsumerParam.builder()
                .consumerGroup(consumerGroup).namesrvAddr(nameSrvAddr).topicName(topicName)
                .messageListener(new PlateLineConsume.Listener(receiveService)).build();
        consumerPlateLine = ConsumeInit.concurrentConsumer(consumerParam);
        consumerPlateLine.start();
        log.info("日K数据消息消费者服务启动成功.");
    }

    @PreDestroy
    public void destroy() {
        log.info("开始关闭日K数据消息消费者服务...");
        consumerPlateLine.shutdown();
        log.info("日K数据消息消费者服务已关闭.");
    }

    private static class Listener implements MessageListenerConcurrently {
        private TushareService receiveService;
        public Listener(TushareService receiveService){
            this.receiveService = receiveService;
        }
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
                                                        ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            for (MessageExt ext : list) {
                try {
                    String record = new String(ext.getBody(), RemotingHelper.DEFAULT_CHARSET);
                    Map map = JSON.parseObject(record, Map.class);
                    receiveService.receivePlateLine(map);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("PLATE数据开始消费");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}
