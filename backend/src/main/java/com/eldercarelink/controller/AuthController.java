package com.eldercarelink.controller;

import com.eldercarelink.common.ApiResponse;
import com.eldercarelink.dto.ChangePasswordRequest;
import com.eldercarelink.dto.LoginRequest;
import com.eldercarelink.entity.User;
import com.eldercarelink.service.TokenService;
import com.eldercarelink.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录认证相关接口。
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        String token = tokenService.generateToken(user);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("role", user.getRole());
        result.put("realName", user.getRealName());
        return ApiResponse.success("登录成功", result);
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal User currentUser, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(currentUser, request);
        return ApiResponse.success("修改成功", null);
    }
}
