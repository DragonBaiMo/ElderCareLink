package com.eldercarelink.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新用户请求参数。
 */
@Data
public class UserUpdateRequest {
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    private String phone;

    @NotBlank(message = "角色不能为空")
    private String role;

    private String status;

    private String organization;

    private String remark;
}
