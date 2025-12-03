package com.eldercarelink.service.impl;

import com.eldercarelink.common.BusinessException;
import com.eldercarelink.dto.ChangePasswordRequest;
import com.eldercarelink.entity.User;
import com.eldercarelink.repository.UserRepository;
import com.eldercarelink.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 用户业务实现。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public User login(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        User user = userOpt.orElseThrow(() -> new BusinessException("账号不存在"));
        if (!encoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        if (!user.isEnabled()) {
            throw new BusinessException("账号已停用");
        }
        return user;
    }

    @Override
    public void changePassword(User current, ChangePasswordRequest request) {
        if (!encoder.matches(request.getOldPassword(), current.getPassword())) {
            throw new BusinessException("旧密码校验失败");
        }
        current.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(current);
    }

    @Override
    public User createUser(User user) {
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            throw new BusinessException("用户名或密码不能为空");
        }
        userRepository.findByUsername(user.getUsername()).ifPresent(u -> {
            throw new BusinessException("用户名已存在");
        });
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, User user) {
        User exist = userRepository.findById(userId).orElseThrow(() -> new BusinessException("用户不存在"));
        exist.setPhone(user.getPhone());
        exist.setRealName(user.getRealName());
        exist.setRole(user.getRole());
        exist.setStatus(user.getStatus());
        return userRepository.save(exist);
    }

    @Override
    public void disableUser(Long userId) {
        User exist = userRepository.findById(userId).orElseThrow(() -> new BusinessException("用户不存在"));
        exist.setStatus("DISABLED");
        userRepository.save(exist);
    }

    @Override
    public Page<User> listUsers(Pageable pageable, String role) {
        if (StringUtils.hasText(role)) {
            return userRepository.findByRole(role, pageable);
        }
        return userRepository.findAll(pageable);
    }
}
