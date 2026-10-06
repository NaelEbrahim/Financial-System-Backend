package com.example.novabank.identity.Repository;

import com.example.novabank.identity.Enum.OutboxStatus;
import com.example.novabank.identity.Model.OutBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutBox, Long> {

    List<OutBox> findByStatus(OutboxStatus status);

}
