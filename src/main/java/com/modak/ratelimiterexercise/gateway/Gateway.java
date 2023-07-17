package com.modak.ratelimiterexercise.gateway;

import org.springframework.stereotype.Service;

@Service
public class Gateway {

    public Gateway() {
    }

    public void send(String userId, String message) {
        System.out.printf("Sending user %s message: %s%n", userId, message);
    }
}
