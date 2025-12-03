package com.eldercarelink.dto;

import com.eldercarelink.entity.Elder;
import com.eldercarelink.entity.HealthRecord;
import com.eldercarelink.entity.VisitRecord;
import lombok.Data;

import java.util.List;

/**
 * 老人详情响应，包含基础信息与最近的健康/探访记录摘要。
 */
@Data
public class ElderDetailResponse {
    private Elder elder;
    private List<HealthRecord> recentHealthRecords;
    private List<VisitRecord> recentVisitRecords;
}
