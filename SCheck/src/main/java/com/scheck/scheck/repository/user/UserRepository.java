package com.scheck.scheck.repository.user;

import com.scheck.scheck.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(String kakaoId);
    boolean existsByKakaoId(String kakaoId);
    boolean existsByNickname(String nickname);
}
