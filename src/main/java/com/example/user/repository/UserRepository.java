package com.example.user.repository;

import com.example.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * 사용자 ID로 사용자 조회
     * @param userId 사용자 ID (이메일)
     * @return 사용자 정보
     */
    Optional<User> findByUserId(String userId);
    
    /**
     * 사용자 ID 존재 여부 확인
     * @param userId 사용자 ID (이메일)
     * @return 존재 여부
     */
    boolean existsByUserId(String userId);
    
    /**
     * 닉네임으로 사용자 조회
     * @param nickname 닉네임
     * @return 사용자 정보
     */
    Optional<User> findByNickname(String nickname);
    
    /**
     * 닉네임 존재 여부 확인
     * @param nickname 닉네임
     * @return 존재 여부
     */
    boolean existsByNickname(String nickname);
}
