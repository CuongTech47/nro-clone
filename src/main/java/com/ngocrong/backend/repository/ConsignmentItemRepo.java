package com.ngocrong.backend.repository;

import com.ngocrong.backend.entity.ConsignmentItemEntity;
import com.ngocrong.backend.shop.ConsignmentItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface ConsignmentItemRepo extends JpaRepository<ConsignmentItemEntity, Integer> {
    List<ConsignmentItemEntity> findByStatusIn(ConsignmentItemStatus[] statusList);

    @Modifying
    @Query("UPDATE ConsignmentItemEntity i SET i.buyerId = :buyerId, " +
            "i.status = :status, " +
            "i.options = :options, " +
            "i.buyTime = :buyTime, " +
            "i.receiveTime = :receiveTime " +
            "WHERE i.id = :id")
    void saveData(Long id, Integer buyerId, ConsignmentItemStatus status, String options, Timestamp buyTime, Timestamp receiveTime);
}
