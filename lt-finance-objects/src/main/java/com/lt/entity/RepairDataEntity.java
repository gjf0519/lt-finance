package com.lt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairDataEntity {
    private int id;
    private String repairCode;
    private String repairDate;
    private String repairTopic;
    private int repairNum;
}
