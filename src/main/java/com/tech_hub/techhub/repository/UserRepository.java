package com.tech_hub.techhub.repository;

import com.tech_hub.techhub.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUsername(String username);
    @Query("SELECT u FROM UserEntity u WHERE u.username LIKE %?1%")
    List<UserEntity> findByName(String username);
    Optional<UserEntity> findByEmail(String username);
    UserEntity findByPhoneNumber(Long phoneNumber);

    Optional<UserEntity> findByUsername(String username);

}
