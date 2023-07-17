package com.modak.ratelimiterexercise.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private String type;
    private String user;
    private String message;

//    public NotificationDTO() {
//    }
//
//    public NotificationDTO(String type, String user, String message) {
//        this.type = type;
//        this.user = user;
//        this.message = message;
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
