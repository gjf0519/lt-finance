package com.lt.screen.day;

import com.lt.entity.KLineEntity;
import com.lt.rules.*;
import com.lt.screen.LineFormFilter;
import com.lt.shape.MaLineType;

import java.util.List;
import java.util.Map;

/**
 * @author gaijf
 * @description 连续上升形态过滤
 * @date 2021/1/30
 */
public class DayRiseFormFilter implements LineFormFilter {

    @Override
    public int execute(List<KLineEntity> kLineEntities) {
        //凝聚程度
        MaLineCohereRule maLineCohereRule = new MaLineCohereRule();
        int cohere = maLineCohereRule.verify(kLineEntities);
        //均匀排列
        MaLineArrangeRule maLineArrangeRule = new MaLineArrangeRule();
        int arrange = maLineArrangeRule.verify(kLineEntities.get(0));
        int arrangeLevel = 0;
        if(arrange == 1){
            arrangeLevel = 2;
        } else if(arrange == 0){
            arrangeLevel = 1;
        }else {
            return 0;
        }
        //持续性
        KlineContinueRule klineContinueRule = new KlineContinueRule();
        int continueNum5 = klineContinueRule.verify(kLineEntities,MaLineType.LINE005,5);
        klineContinueRule.setContinue(true);
        int continueNum10 = klineContinueRule.verify(kLineEntities,MaLineType.LINE005,10);
        if(continueNum10 < 5 && continueNum5 < 3){
            return 0;
        }
        //K位置
        SiteKlineMaLineRule siteKlineMaLineRule = new SiteKlineMaLineRule();
        Map<String,Integer> sites = siteKlineMaLineRule.verify(kLineEntities.get(0));
        if(null == sites.get(MaLineType.LINE030.getName()) ||
                null == sites.get(MaLineType.LINE020.getName())){
            return 0;
        }
        int siteLevel = 0;
        if(sites.get(MaLineType.LINE005.getName()) == -1
                && sites.get(MaLineType.LINE010.getName()) == 0
                && sites.get(MaLineType.LINE020.getName()) == 1
                && sites.get(MaLineType.LINE030.getName()) == 1){
            //重点
            siteLevel = 2;
        }else if(sites.get(MaLineType.LINE005.getName()) == 0 //600379 20210401
                && sites.get(MaLineType.LINE010.getName()) == 0
                && sites.get(MaLineType.LINE020.getName()) == 1
                && sites.get(MaLineType.LINE030.getName()) == 1){
            //重点
            siteLevel = 2;
        }else if(sites.get(MaLineType.LINE005.getName()) == 0 //000793 20210406
                && sites.get(MaLineType.LINE010.getName()) == 1
                && sites.get(MaLineType.LINE020.getName()) == 1
                && sites.get(MaLineType.LINE030.getName()) == 1){
            //重点
            siteLevel = 2;
        }else if(sites.get(MaLineType.LINE020.getName()) == 1
                && sites.get(MaLineType.LINE030.getName()) == 1){
            //普通
            siteLevel = 1;
        }else if(continueNum10 >=7 && sites.get(MaLineType.LINE020.getName()) == 1){
            //普通
            siteLevel = 1;
        }else {
            return 0;
        }
        //K距离
        KmKlineMaLineRule kmKlineMaLineRule = new KmKlineMaLineRule();
        double km = kmKlineMaLineRule.verify(kLineEntities.get(0));
        if(arrangeLevel == 2 || continueNum10 >= 8){
            if(km > 0.06 || km < -0.01){
                return 0;
            }
        }else {
            if(km > 0.01 || km < -0.01){
                return 0;
            }
        }
        //5日内回踩或拐头
        DownMaLineRule downMaLineRule = new DownMaLineRule();
        int dw = downMaLineRule.verify(kLineEntities);//-1破线或连续下跌2次0回踩1拐头
        if(dw == -1 && cohere == 0){
            return 0;
        }
        //15日内是否有过涨停
        LineRoseRule longSunRule = new LineRoseRule(4,4.9,21);
        int site = longSunRule.verify(kLineEntities,15);
        //均线振幅过滤
        if(arrangeLevel == 2){
            LineRoseRule mlineRoseRule = new LineRoseRule(0.12,-0.03);
            int mrose = mlineRoseRule.verify(kLineEntities,10);
            if(mrose == 0){
                return 0;
            }
        }else if(site > 6){
            LineRoseRule mlineRoseRule = new LineRoseRule(0.1,-0.03);
            int mrose = mlineRoseRule.verify(kLineEntities,10);
            if(mrose == 0){
                return 0;
            }
        }else {
            LineRoseRule mlineRoseRule = new LineRoseRule(0.08,-0.03);
            int mrose = mlineRoseRule.verify(kLineEntities,10);
            if(mrose == 0){
                return 0;
            }
        }
        //K线振幅过滤
        int krose = 0;
        if(arrangeLevel == 2){
            LineRoseRule klineRoseRule = new LineRoseRule(2,8,-8);
            krose = klineRoseRule.verify(kLineEntities,5);
        }else {
            LineRoseRule klineRoseRule = new LineRoseRule(2,5.9,-4.9);
            krose = klineRoseRule.verify(kLineEntities,10);
        }
        if(krose == 0){
            return 0;
        }
        //振幅小于3大于-3数量
        LineRoseRule roseNums = new LineRoseRule(1,3,-3);
        int nums = roseNums.verify(kLineEntities,10);
        if(nums < 6){
            return 0;
        }
        if(siteLevel == 2 && arrangeLevel == 2){
            return 4;
        }
        if(siteLevel != 2 && arrangeLevel == 2){
            return 3;
        }
        if(siteLevel == 2 && arrangeLevel != 2){
            return 2;
        }
        //重要突破
//        System.out.println(kLineEntities.get(0).getTsCode()+"======================"+siteLevel+"==========================="+arrangeLevel);
        return 1;
    }
}
