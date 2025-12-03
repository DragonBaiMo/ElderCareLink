package com.eldercarelink.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新建用户请求参数。
 */
@Data
public class UserCreateRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    private String phone;

    @NotBlank(message = "角色不能为空")
    private String role;

    private String organization;

    private String remark;
}
