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
 * @description 日K消费者
 * @date 2020/12/3
 */
@Slf4j
@Component
public class WeekLineConsume {

    @Value("${rocketmq.name-server}")
    private String nameSrvAddr;
    @Value("${rocketmq.comsumer.week-line}")
    private String consumerGroup;
    @Autowired
    private TushareService tushareService;
    private DefaultMQPushConsumer consumerWeekLine;
    private String topicName = TushareUtil.TUSHARE_WEEKLINE_TOPIC;

    @PostConstruct
    public void init() throws Exception {
        log.info("开始启动周K数据消费者服务...");
        ConsumeInit.ConsumerParam consumerParam = ConsumeInit.ConsumerParam.builder()
                .consumerGroup(consumerGroup).namesrvAddr(nameSrvAddr).topicName(topicName)
                .messageListener(new WeekLineConsume.Listener(tushareService)).build();
        consumerWeekLine = ConsumeInit.concurrentConsumer(consumerParam);
        consumerWeekLine.start();
        log.info("周K数据消息消费者服务启动成功.");
    }

    @PreDestroy
    public void destroy() {
        log.info("开始关闭周K数据消息消费者服务...");
        consumerWeekLine.shutdown();
        log.info("周K数据消息消费者服务已关闭.");
    }

    private static class Listener implements MessageListenerConcurrently {
        private TushareService tushareService;
        public Listener(TushareService receiveService){
            this.tushareService = receiveService;
        }
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
                                                        ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            for (MessageExt ext : list) {
                try {
                    String record = new String(ext.getBody(), RemotingHelper.DEFAULT_CHARSET);
                    Map<String,String> map =  JSON.parseObject(record, Map.class);
                    tushareService.receiveWeekLine(map);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("周K数据开始消费");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}
