package com.eldercarelink.service.impl;

import com.eldercarelink.common.BusinessException;
import com.eldercarelink.dto.ElderDetailResponse;
import com.eldercarelink.entity.Elder;
import com.eldercarelink.entity.HealthRecord;
import com.eldercarelink.entity.User;
import com.eldercarelink.entity.VisitRecord;
import com.eldercarelink.repository.ElderRepository;
import com.eldercarelink.repository.HealthRecordRepository;
import com.eldercarelink.repository.UserRepository;
import com.eldercarelink.repository.VisitRecordRepository;
import com.eldercarelink.service.ElderService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * 老人信息业务实现，封装软删除、导出与详情聚合能力。
 */
@Service
@RequiredArgsConstructor
public class ElderServiceImpl implements ElderService {
    private final ElderRepository elderRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final VisitRecordRepository visitRecordRepository;
    private final UserRepository userRepository;

    @Override
    public Elder create(Elder elder) {
        fillVolunteerName(elder);
        return elderRepository.save(elder);
    }

    @Override
    public Elder update(Long id, Elder elder) {
        Elder exist = elderRepository.findById(id).orElseThrow(() -> new BusinessException("老人不存在"));
        elder.setId(exist.getId());
        elder.setCreateTime(exist.getCreateTime());
        fillVolunteerName(elder);
        return elderRepository.save(elder);
    }

    @Override
    public void disable(Long id) {
        Elder elder = elderRepository.findById(id).orElseThrow(() -> new BusinessException("老人不存在"));
        elder.setStatus("DISABLED");
        elderRepository.save(elder);
    }

    @Override
    public Page<Elder> list(String name, String livingType, String healthLevel, String status, Long volunteerId, Pageable pageable) {
        String statusFilter = StringUtils.hasText(status) ? status : "ACTIVE";
        if (volunteerId != null) {
            return elderRepository.findByResponsibleVolunteerIdAndStatusContaining(volunteerId, statusFilter, pageable);
        }
        return elderRepository.findByNameContainingAndLivingTypeContainingAndHealthLevelContainingAndStatusContaining(
                Optional.ofNullable(name).orElse(""),
                Optional.ofNullable(livingType).orElse(""),
                Optional.ofNullable(healthLevel).orElse(""),
                statusFilter,
                pageable
        );
    }

    @Override
    public ElderDetailResponse detail(Long id) {
        Elder elder = elderRepository.findById(id).orElseThrow(() -> new BusinessException("老人不存在"));
        List<HealthRecord> healthRecords = healthRecordRepository.findTop5ByElderIdOrderByRecordDateDesc(id);
        List<VisitRecord> visitRecords = visitRecordRepository.findTop5ByElderIdOrderByVisitTimeDesc(id);
        ElderDetailResponse response = new ElderDetailResponse();
        response.setElder(elder);
        response.setRecentHealthRecords(healthRecords);
        response.setRecentVisitRecords(visitRecords);
        return response;
    }

    @Override
    public ResponseEntity<byte[]> exportAll() {
        List<Elder> elders = elderRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("老人信息");
        String[] headers = new String[]{"姓名", "性别", "年龄", "居住类型", "健康等级", "电话", "住址", "紧急联系人", "紧急电话", "负责志愿者", "状态"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        int rowIndex = 1;
        for (Elder elder : elders) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(safeString(elder.getName()));
            row.createCell(1).setCellValue(safeString(elder.getGender()));
            row.createCell(2).setCellValue(safeString(elder.getAge()));
            row.createCell(3).setCellValue(safeString(elder.getLivingType()));
            row.createCell(4).setCellValue(safeString(elder.getHealthLevel()));
            row.createCell(5).setCellValue(safeString(elder.getPhone()));
            row.createCell(6).setCellValue(safeString(elder.getAddress()));
            row.createCell(7).setCellValue(safeString(elder.getEmergencyContactName()));
            row.createCell(8).setCellValue(safeString(elder.getEmergencyContactPhone()));
            row.createCell(9).setCellValue(safeString(elder.getResponsibleVolunteerName()));
            row.createCell(10).setCellValue(safeString(elder.getStatus()));
        }
        autoSizeColumns(sheet, headers.length);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            workbook.close();
            String fileName = URLEncoder.encode("老人信息导出.xlsx", StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        } catch (IOException e) {
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }

    private void fillVolunteerName(Elder elder) {
        if (elder.getResponsibleVolunteerId() == null) {
            elder.setResponsibleVolunteerName(null);
            return;
        }
        userRepository.findById(elder.getResponsibleVolunteerId()).ifPresent(user -> elder.setResponsibleVolunteerName(user.getRealName()));
    }

    private String safeString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
