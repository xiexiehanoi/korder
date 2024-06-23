package com.hanghae.korder.user.repository;

import com.hanghae.korder.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByName(String name);
    Optional<UserEntity> findByEmail(String email);

}
