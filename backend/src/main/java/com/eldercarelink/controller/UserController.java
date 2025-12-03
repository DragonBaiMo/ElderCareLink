package com.eldercarelink.controller;

import com.eldercarelink.common.ApiResponse;
import com.eldercarelink.dto.UserCreateRequest;
import com.eldercarelink.dto.UserUpdateRequest;
import com.eldercarelink.entity.User;
import com.eldercarelink.entity.VolunteerProfile;
import com.eldercarelink.repository.VolunteerProfileRepository;
import com.eldercarelink.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * 用户管理接口，支持账号增删改查与志愿者资料维护。
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final VolunteerProfileRepository volunteerProfileRepository;

    @GetMapping
    public ApiResponse<Page<User>> list(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "") String role) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> pageResult = userService.listUsers(pageable, role);
        fillVolunteerInfo(pageResult.getContent());
        return ApiResponse.success(pageResult);
    }

    @PostMapping
    public ApiResponse<User> create(@Valid @RequestBody UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus("NORMAL");
        User saved = userService.createUser(user);
        saveVolunteerProfileIfNeed(saved.getId(), request.getRole(), request.getOrganization(), request.getRemark());
        saved.setOrganization(request.getOrganization());
        saved.setRemark(request.getRemark());
        return ApiResponse.success(saved);
    }

    @PutMapping("/{id}")
    public ApiResponse<User> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        User user = new User();
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        User saved = userService.updateUser(id, user);
        saveVolunteerProfileIfNeed(saved.getId(), request.getRole(), request.getOrganization(), request.getRemark());
        saved.setOrganization(request.getOrganization());
        saved.setRemark(request.getRemark());
        return ApiResponse.success(saved);
    }

    @PatchMapping("/{id}/disable")
    public ApiResponse<Void> disable(@PathVariable Long id) {
        userService.disableUser(id);
        volunteerProfileRepository.findByUserId(id).ifPresent(volunteerProfileRepository::delete);
        return ApiResponse.success("已禁用", null);
    }

    @GetMapping("/me")
    public ApiResponse<User> current(@AuthenticationPrincipal User user) {
        return ApiResponse.success(user);
    }

    private void saveVolunteerProfileIfNeed(Long userId, String role, String organization, String remark) {
        if (!"VOLUNTEER".equalsIgnoreCase(role)) {
            volunteerProfileRepository.findByUserId(userId).ifPresent(volunteerProfileRepository::delete);
            return;
        }
        VolunteerProfile profile = volunteerProfileRepository.findByUserId(userId).orElseGet(() -> {
            VolunteerProfile p = new VolunteerProfile();
            p.setUserId(userId);
            return p;
        });
        profile.setOrganization(organization);
        profile.setRemark(remark);
        volunteerProfileRepository.save(profile);
    }

    private void fillVolunteerInfo(java.util.List<User> users) {
        java.util.List<Long> ids = users.stream().map(User::getId).toList();
        java.util.Map<Long, VolunteerProfile> map = volunteerProfileRepository.findByUserIdIn(ids).stream()
                .collect(java.util.stream.Collectors.toMap(VolunteerProfile::getUserId, p -> p));
        users.forEach(u -> {
            VolunteerProfile profile = map.get(u.getId());
            if (profile != null) {
                u.setOrganization(profile.getOrganization());
                u.setRemark(profile.getRemark());
            }
        });
    }
}
