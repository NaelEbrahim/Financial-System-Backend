package com.example.novabank.notification.Repository;

import com.example.novabank.notification.Model.NotificationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationModel, Long> {

    @Query("""
                SELECT n
                FROM notification n
                WHERE n.userId = :userId
                ORDER BY n.id DESC
            """)
    Page<NotificationModel> findByUserId(Long userId, Pageable pageable);

}
