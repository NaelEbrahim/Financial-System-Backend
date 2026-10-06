package com.example.novabank.account.Repository;

import com.example.novabank.account.Enum.OutboxStatus;
import com.example.novabank.account.Model.OutBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutBox, Long> {

    List<OutBox> findByStatus(OutboxStatus status);

}
