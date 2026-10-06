package com.example.novabank.transaction.Repository;

import com.example.novabank.transaction.Enum.OutboxStatus;
import com.example.novabank.transaction.Model.OutBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutBox, Long> {

    List<OutBox> findByStatus(OutboxStatus status);

}
