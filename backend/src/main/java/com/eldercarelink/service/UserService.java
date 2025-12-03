package com.eldercarelink.service;

import com.eldercarelink.dto.ChangePasswordRequest;
import com.eldercarelink.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 用户相关业务接口。
 */
public interface UserService {
    User login(String username, String rawPassword);

    void changePassword(User current, ChangePasswordRequest request);

    User createUser(User user);

    User updateUser(Long userId, User user);

    void disableUser(Long userId);

    Page<User> listUsers(Pageable pageable, String role);
}
