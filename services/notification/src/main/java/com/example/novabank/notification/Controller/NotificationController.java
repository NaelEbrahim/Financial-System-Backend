package com.example.novabank.notification.Controller;

import com.example.novabank.notification.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/get-user-notifications")
    public ResponseEntity<?> getNotifications(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        if (page == null || size == null) {
            var response = notificationService.getUserNotifications(null);
            return ResponseEntity.ok(Map.of("message", response));
        } else {
            Pageable pageable = PageRequest.of(page, size);
            var response = notificationService.getUserNotifications(pageable);
            return ResponseEntity.ok(Map.of("message", response));
        }
    }

    @DeleteMapping("/delete-notification/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        var response = notificationService.deleteNotification(id);
        return ResponseEntity.ok(Map.of("message", response));
    }

    @PutMapping("/mark-notification-read/{id}")
    public ResponseEntity<?> markNotificationRead(@PathVariable Long id) {
        var response = notificationService.markNotificationRead(id);
        return ResponseEntity.ok(Map.of("message", response));
    }

}
