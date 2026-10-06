package com.example.novabank.account.Service;

import com.example.novabank.account.Enum.OutBoxEventType;
import com.example.novabank.account.Enum.OutboxStatus;
import com.example.novabank.account.Model.OutBox;
import com.example.novabank.account.Repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final ObjectMapper objectMapper;

    private final OutboxRepository outboxRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void saveEvent(OutBoxEventType eventType, Object event)
            throws JsonProcessingException {

        OutBox newOutbox = new OutBox();
        newOutbox.setEventType(eventType);
        newOutbox.setPayload(objectMapper.writeValueAsString(event));

        outboxRepository.save(newOutbox);
    }


    @Transactional
    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {
        List<OutBox> events =
                outboxRepository.findByStatus(OutboxStatus.PENDING);

        for (OutBox event : events) {
            try {
                kafkaTemplate.send(
                        event.getEventType().name(),
                        event.getPayload()
                );

                event.setStatus(OutboxStatus.PUBLISHED);

            } catch (Exception ex) {
                event.setRetryCount(event.getRetryCount() + 1);

                event.setStatus(OutboxStatus.FAILED);
            }
        }
    }

}