package com.modak.ratelimiterexercise.service;

import com.modak.ratelimiterexercise.gateway.Gateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationsService {

    private final Gateway gateway;

    @Autowired
    public NotificationsService(Gateway gateway) {
        this.gateway = gateway;
    }

    public void sendNotification(String userId, String message) {
        gateway.send(userId, message);
    }
}
