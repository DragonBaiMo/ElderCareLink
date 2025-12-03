package com.eldercarelink.service;

import com.eldercarelink.entity.User;

import java.util.Optional;

/**
 * 生成与解析登录 Token 的接口。
 */
public interface TokenService {
    String generateToken(User user);

    Optional<User> parseToken(String token);
}
