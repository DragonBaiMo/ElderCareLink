package com.eldercarelink.controller;

import com.eldercarelink.common.ApiResponse;
import com.eldercarelink.entity.VisitRecord;
import com.eldercarelink.repository.ElderRepository;
import com.eldercarelink.repository.HealthRecordRepository;
import com.eldercarelink.repository.UserRepository;
import com.eldercarelink.repository.VisitRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 数据统计接口，返回简单汇总数字。
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final ElderRepository elderRepository;
    private final VisitRecordRepository visitRecordRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ApiResponse<Map<String, Object>> overview() {
        Map<String, Object> result = new HashMap<>();
        result.put("elderCount", elderRepository.count());
        result.put("visitCount", visitRecordRepository.count());
        result.put("healthRecordCount", healthRecordRepository.count());
        result.put("healthLevelDistribution", buildHealthLevelDistribution());
        result.put("visitTrend", buildVisitTrend());
        result.put("volunteerRanking", buildVolunteerRanking());
        return ApiResponse.success(result);
    }

    private Map<String, Long> buildHealthLevelDistribution() {
        return elderRepository.findAll().stream()
                .collect(Collectors.groupingBy(elder -> Optional.ofNullable(elder.getHealthLevel()).orElse("未填写"), Collectors.counting()));
    }

    private Map<String, Long> buildVisitTrend() {
        return visitRecordRepository.findAll().stream()
                .filter(v -> v.getVisitTime() != null)
                .collect(Collectors.groupingBy(v -> v.getVisitTime().toLocalDate().toString(), Collectors.counting()));
    }

    private Map<String, Long> buildVolunteerRanking() {
        Map<Long, Long> raw = visitRecordRepository.findAll().stream()
                .filter(v -> v.getVolunteerId() != null)
                .collect(Collectors.groupingBy(VisitRecord::getVolunteerId, Collectors.counting()));
        Map<Long, String> names = userRepository.findAllById(raw.keySet()).stream()
                .collect(Collectors.toMap(user -> user.getId(), user -> user.getRealName()));
        Map<String, Long> result = new HashMap<>();
        raw.forEach((id, cnt) -> result.put(names.getOrDefault(id, "志愿者" + id), cnt));
        return result;
    }
}
