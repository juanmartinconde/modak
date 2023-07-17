package com.modak.ratelimiterexercise.controller;

import com.modak.ratelimiterexercise.exceptions.ApiException;
import com.modak.ratelimiterexercise.model.NotificationDTO;
import com.modak.ratelimiterexercise.ratelimiter.RateLimitConfig;
import com.modak.ratelimiterexercise.service.NotificationsService;
import io.github.bucket4j.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("notifications")
public class NotificationsController {

    private static final Logger log = LoggerFactory.getLogger(NotificationsController.class);
    private final NotificationsService notificationsService;
    private final RateLimitConfig rateLimiter;

    @Autowired
    public NotificationsController(NotificationsService notificationsService, RateLimitConfig rateLimiter) {
        this.notificationsService = notificationsService;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/send")
    public NotificationDTO sendNotificationNew(@RequestBody NotificationDTO dto) {
        validateNotificationDTO(dto);
        validateQuota(dto.getType(), dto.getUser());
        notificationsService.sendNotification(dto.getUser(), dto.getMessage());
        return dto;
    }

    private void validateNotificationDTO(NotificationDTO dto) {
        if (dto.getType() == null || Objects.equals(dto.getType(), "")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "bad.request", "Type field must be present and not empty");
        }
        if (dto.getUser() == null || Objects.equals(dto.getUser(), "")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "bad.request", "User field must be present and not empty");
        }
        if (dto.getMessage() == null || Objects.equals(dto.getMessage(), "")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "bad.request", "Message field must be present and not empty");
        }
    }

    private void validateQuota(String type, String user) {
        Bucket bucket = rateLimiter.resolveBucket(type, user);
        if (!bucket.tryConsume(1)){
            String error = String.format("User %s has consumed available quota for %s operation", user, type);
            log.error(error);
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "too.many.requests", error);
        }
    }

}
