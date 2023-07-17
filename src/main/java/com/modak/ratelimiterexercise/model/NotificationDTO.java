package com.modak.ratelimiterexercise.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotificationDTO {
    private String type;
    private String user;
    private String message;
}
