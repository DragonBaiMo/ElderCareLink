package com.eldercarelink.repository;

import com.eldercarelink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户仓储接口，提供基础 CRUD 与按用户名查询。
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Page<User> findByRole(String role, Pageable pageable);
}
