package com.lt.screen;

import com.lt.entity.KLineEntity;

import java.util.List;

public interface LineFormFilter {
    int execute(List<KLineEntity> kLineEntities);
}
