package com.lt.screen;

import com.lt.entity.KLineEntity;

import java.util.List;

public interface RiseFormFilter {
    int execute(List<KLineEntity> kLineEntities);
}
