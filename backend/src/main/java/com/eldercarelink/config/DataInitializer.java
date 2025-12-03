package com.eldercarelink.config;

import com.eldercarelink.entity.*;
import com.eldercarelink.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 启动时预置基础数据，便于答辩演示。
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ElderRepository elderRepository;
    private final DictRepository dictRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final VisitRecordRepository visitRecordRepository;
    private final VolunteerProfileRepository volunteerProfileRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 由 scripts/generate_secrets.py 生成的默认密码哈希，保证凭据可追溯。
     */
    @Value("${security.default-password-hash:}")
    private String defaultPasswordHash;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            String encodedPassword = resolveDefaultPassword();
            User admin = new User(null, "admin", encodedPassword, "管理员", "13800000000", "ADMIN", "NORMAL", LocalDateTime.now());
            User volunteer = new User(null, "vol01", encodedPassword, "志愿者一号", "13900000000", "VOLUNTEER", "NORMAL", LocalDateTime.now());
            userRepository.saveAll(Arrays.asList(admin, volunteer));
            VolunteerProfile profile = new VolunteerProfile();
            profile.setUserId(volunteer.getId());
            profile.setOrganization("社区志愿者协会");
            profile.setRemark("主要负责 1 栋");
            volunteerProfileRepository.save(profile);
        }
        if (dictRepository.count() == 0) {
            dictRepository.saveAll(Arrays.asList(
                    buildDict("living_type", "alone", "独居", 1),
                    buildDict("living_type", "empty", "空巢", 2),
                    buildDict("health_level", "good", "良好", 1),
                    buildDict("health_level", "focus", "重点关注", 2)
            ));
        }
        if (elderRepository.count() == 0) {
            Elder elder = new Elder();
            elder.setName("王阿姨");
            elder.setGender("女");
            elder.setAge(72);
            elder.setPhone("13600000000");
            elder.setAddress("幸福小区 1 栋 302");
            elder.setLivingType("empty");
            elder.setHealthLevel("focus");
            elder.setEmergencyContactName("王女儿");
            elder.setEmergencyContactPhone("13911112222");
            elder.setResponsibleVolunteerId(userRepository.findByUsername("vol01").orElseThrow().getId());
            elder.setResponsibleVolunteerName("志愿者一号");
            elderRepository.save(elder);
        }
        if (healthRecordRepository.count() == 0) {
            Elder elder = elderRepository.findAll().get(0);
            HealthRecord record = new HealthRecord();
            record.setElderId(elder.getId());
            record.setRecordDate(LocalDateTime.now().minusDays(1));
            record.setBloodPressure("125/85");
            record.setHeartRate(78);
            record.setTemperature(36.7);
            record.setHealthDesc("状态良好");
            record.setCreatedBy(userRepository.findByUsername("admin").orElseThrow().getId());
            healthRecordRepository.save(record);
        }
        if (visitRecordRepository.count() == 0) {
            Elder elder = elderRepository.findAll().get(0);
            VisitRecord visitRecord = new VisitRecord();
            visitRecord.setElderId(elder.getId());
            visitRecord.setVolunteerId(userRepository.findByUsername("vol01").orElseThrow().getId());
            visitRecord.setVisitTime(LocalDateTime.now().minusDays(2));
            visitRecord.setVisitType("入户探访");
            visitRecord.setServiceContent("陪同聊天，查看生活用品");
            visitRecord.setElderStatus("精神良好");
            visitRecord.setNextAdvice("保持每周一次探访");
            visitRecordRepository.save(visitRecord);
        }
    }

    private DictEntry buildDict(String type, String key, String value, int sort) {
        DictEntry entry = new DictEntry();
        entry.setDictType(type);
        entry.setDictKey(key);
        entry.setDictValue(value);
        entry.setSort(sort);
        return entry;
    }

    private String resolveDefaultPassword() {
        if (StringUtils.hasText(defaultPasswordHash)) {
            return defaultPasswordHash;
        }
        // 兜底逻辑：若未配置哈希则重新编码固定口令，保证初始化可用
        return passwordEncoder.encode("123456");
    }
}
