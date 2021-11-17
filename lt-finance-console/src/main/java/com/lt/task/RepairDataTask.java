package com.lt.task;

import com.lt.entity.RepairDataEntity;
import com.lt.mapper.ReceiveMapper;
import com.lt.service.KLineService;
import com.lt.utils.Constants;
import com.lt.utils.RestTemplateUtil;
import com.lt.utils.TushareUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
public class RepairDataTask {

    @Autowired
    private KLineService kLineService;
    @Resource
    private ReceiveMapper receiveMapper;

    @Scheduled(cron = "0 0/30 * * * ? ")
    public void execute() {
        //日K数据补充
        this.dayLineRepair();
        //板块K线数据补充
//        this.plateLineRepair();
    }

    private void dayLineRepair(){
        List<RepairDataEntity> list = kLineService.queryRepairData(new Date());
        if(null == list || list.isEmpty()){
            return;
        }
        //检查数据是否已补充
        for(RepairDataEntity entity : list){
            int isSave = receiveMapper.hasSaveDayLine(entity.getRepairCode(),entity.getRepairDate());
            if(isSave > 0){
                kLineService.deleteRepairById(entity.getId());
                continue;
            }
            if(TushareUtil.TUSHARE_DAYLINE_TOPIC.equals(entity.getRepairTopic())){
                RestTemplateUtil.get("http://101.200.170.91:9090/day/line/"+entity.getRepairCode());
                kLineService.updateRepairById(entity.getId());
            }
        }
    }

    private void plateLineRepair(){
        int total = kLineService.queryCountByDate(new Date());
        if(total < 829){
            RestTemplateUtil.get("http://101.200.170.91:9090/plate/line/all");
        }
    }
}
