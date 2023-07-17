package com.modak.ratelimiterexercise.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modak.ratelimiterexercise.model.NotificationDTO;
import okhttp3.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.cache.CacheManager;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class NotificationsControllerTests {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String POST_URL = BASE_URL + "/notifications/send";
    private static OkHttpClient client = new OkHttpClient();
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        cacheManager.getCache("cache").clear();
    }

    @AfterAll
    static void clearUp() {

    }

    @Test
    void testSendNotificationShouldWorkWhenAvailableQuota() {
        RestTemplate restTemplate = new RestTemplate();
        NotificationDTO request = new NotificationDTO("status", "pepe", "status1");

        ResponseEntity<String> response = restTemplate.postForEntity(POST_URL, request, String.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    }

    @Test
    void testSendNotificationShouldNotWorkWhenQuotaConsumed() throws Exception {
        String body = getNotificationsBody("news", "pepe", "status1");

        Request request = new Request.Builder()
                .url(POST_URL)
                .post(RequestBody.create(MediaType.parse("application/json"), body))
                .build();
        client.newCall(request).execute();

        // Second apicall should be rate limited
        Map<String, Object> mapResponse = new HashMap<>();
        try (Response response = client.newCall(request).execute()) {
            assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), response.code());
            mapResponse = objectMapper.readValue(response.body().string(), new TypeReference<>() {
            });
        }

        Assertions.assertEquals("too.many.requests", mapResponse.get("code"));
        Assertions.assertEquals("User pepe has consumed available quota for news operation", mapResponse.get("msg"));
    }

    private String getNotificationsBody(String type, String user, String msg) throws JsonProcessingException {
        Map<String, Object> body = new HashMap<>();
        body.put("type", type);
        body.put("user", user);
        body.put("message", msg);
        return objectMapper.writeValueAsString(body);
    }

}
