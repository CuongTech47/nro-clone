package com.ngocrong.backend.repository;


import com.ngocrong.backend.entity.DiscipleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscipleRepo extends JpaRepository<DiscipleEntity, Integer> {
}
