package com.lt.mq;

import com.alibaba.fastjson.JSON;
import com.lt.entity.RepairDataEntity;
import com.lt.service.TushareService;
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

/**
 * @author gaijf
 * @description 补充数据消费
 * @date 2020/12/3
 */
@Slf4j
@Component
public class ConsumeRepairData {
    @Value("${rocketmq.name-server}")
    private String nameSrvAddr;
    @Value("${rocketmq.comsumer.repair-data}")
    private String consumerGroup;
    @Autowired
    private TushareService receiveService;
    private DefaultMQPushConsumer consumerRepair;
    private String topicName = TushareUtil.TUSHARE_REPAIR_TOPIC;

    @PostConstruct
    public void init() throws Exception {
        log.info("开始启动补充数据消费者服务...");
        ConsumeInit.ConsumerParam consumerParam = ConsumeInit.ConsumerParam.builder()
                .consumerGroup(consumerGroup).namesrvAddr(nameSrvAddr).topicName(topicName)
                .messageListener(new ConsumeRepairData.Listener(receiveService)).build();
        consumerRepair = ConsumeInit.concurrentConsumer(consumerParam);
        consumerRepair.start();
        log.info("补充数据消息消费者服务启动成功.");
    }

    @PreDestroy
    public void destroy() {
        log.info("开始关闭补充数据消息消费者服务...");
        consumerRepair.shutdown();
        log.info("补充数据消息消费者服务已关闭.");
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
                    RepairDataEntity repairDataEntity = JSON.parseObject(record, RepairDataEntity.class);
                    repairDataEntity.setRepairNum(0);
                    receiveService.receiveRepairData(repairDataEntity);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("补充数据开始消费");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}
