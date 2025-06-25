package com.example.javatestapi.util;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

public class GraphQLClient {

    public static String fetchAboutMeProfile(int userId) {
        String graphqlEndpoint = "http://localhost:4000/graphql";

        String query = """
            query($userId: Int!) {
              aboutMe(userId: $userId) {
                id
                bio
              }
            }
        """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("userId", userId);

        Map<String, Object> body = new HashMap<>();
        body.put("query", query);
        body.put("variables", variables);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(graphqlEndpoint, entity, String.class);

        return response.getBody();
    }
}
