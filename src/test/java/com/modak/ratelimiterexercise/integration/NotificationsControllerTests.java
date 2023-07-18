package com.modak.ratelimiterexercise.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import javax.cache.CacheManager;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class NotificationsControllerTests {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String POST_URL = BASE_URL + "/notifications/send";
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        cacheManager.getCache("cache").clear();
    }

    @Test
    void testSendNotificationShouldWorkWhenAvailableQuota() throws Exception {
        String body = getNotificationsBody("status", "pepe", "status1");

        Request request = getNotificationsPostRequest(body);
        Response response = client.newCall(request).execute();
        assertEquals(HttpStatus.OK.value(), response.code());
    }

    @Test
    void testSendNotificationShouldNotWorkWhenQuotaConsumed() throws Exception {
        String body = getNotificationsBody("news", "pepe", "news");

        Request request = getNotificationsPostRequest(body);
        Response response = client.newCall(request).execute();
        assertEquals(HttpStatus.OK.value(), response.code());

        // Second apicall should be rate limited
        Response newResponse = client.newCall(request).execute();
        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), newResponse.code());
        Map<String, Object> mapResponse = objectMapper.readValue(newResponse.body().string(), new TypeReference<>() {});
        assertEquals("too.many.requests", mapResponse.get("code"));
        assertEquals("User pepe has consumed available quota for news operation", mapResponse.get("msg"));
    }

    @Test
    void testSendNotificationForSameUserAnotherTypeWhenQuotaOfOtherTypeConsumedShouldWork() throws Exception {
        String userNewsType = getNotificationsBody("news", "user1", "news");
        String userStatusType = getNotificationsBody("status", "user1", "news");

        // Request for news type should work
        Request request = getNotificationsPostRequest(userNewsType);
        Response response = client.newCall(request).execute();
        assertEquals(HttpStatus.OK.value(), response.code());

        // Request for news type should fail, quota consumed
        response = client.newCall(request).execute();
        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), response.code());

        // Request for status type should work, different quota
        request = getNotificationsPostRequest(userStatusType);
        response = client.newCall(request).execute();
        assertEquals(HttpStatus.OK.value(), response.code());
    }

    @Test
    void testSendNotificationForUnknownTypeShouldUseDefaultLimiter() throws Exception {
        String userNewsType = getNotificationsBody("unknown", "user1", "news");

        Request request = getNotificationsPostRequest(userNewsType);
        for (int i = 0; i < 4 ; i++) {
            Response response = client.newCall(request).execute();
            assertEquals(HttpStatus.OK.value(), response.code());
        }

        // Fifth call should be limited
        Response response = client.newCall(request).execute();
        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), response.code());
    }

    private String getNotificationsBody(String type, String user, String msg) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("type", type);
        body.put("user", user);
        body.put("message", msg);
        return objectMapper.writeValueAsString(body);
    }

    private Request getNotificationsPostRequest(String body) {
         return new Request.Builder()
                .url(POST_URL)
                .post(RequestBody.create(MediaType.parse("application/json"), body))
                .build();
    }

}
