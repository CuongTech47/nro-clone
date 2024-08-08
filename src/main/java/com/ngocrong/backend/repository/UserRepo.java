package com.ngocrong.backend.repository;


import com.ngocrong.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface UserRepo extends JpaRepository<UserEntity,Integer> {
    List<UserEntity> findByUsernameAndPassword(String username, String password);
}
