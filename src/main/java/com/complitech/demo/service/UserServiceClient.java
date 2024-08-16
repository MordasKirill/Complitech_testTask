package com.complitech.demo.service;



import com.complitech.demo.entity.AuthenticationRequest;
import com.complitech.demo.entity.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
public class UserServiceClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${server.port}")
    private int serverPort;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public UserServiceClient(ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    /**
     * Get JWT token using admin credentials
     *
     * @return JWT token of type String
     * @throws IOException in case conversion issues
     * @throws URISyntaxException in case incorrect uri
     * @throws InterruptedException in case send interruption
     */
    private String getToken() throws IOException, URISyntaxException, InterruptedException {

        String authRequestBody = objectMapper.writeValueAsString(new AuthenticationRequest("test", "test"));

        URI authUri = new URI("http://localhost:" + serverPort + contextPath + "/api/v1/authenticate");

        HttpRequest authRequestHttp = HttpRequest.newBuilder()
                .uri(authUri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(authRequestBody))
                .build();

        HttpResponse<String> authResponse = httpClient.send(authRequestHttp, HttpResponse.BodyHandlers.ofString());

        if (authResponse.statusCode() == 200) {
            String responseBody = authResponse.body();
            ObjectNode responseJson = (ObjectNode) objectMapper.readTree(responseBody);
            return responseJson.get("jwt").asText();
        } else {
            log.error("Failed to authenticate: " + authResponse.body());
            return "";
        }
    }

    /**
     * Create user in the system service
     *
     * @param userDTO user object
     * @throws Exception if user creation failed
     */
    public void createUserRequest(UserDTO userDTO) throws Exception {
        String requestBody = objectMapper.writeValueAsString(userDTO);

        URI uri = new URI("http://localhost:" + serverPort + contextPath + "/api/v1/users");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            log.info("User created successfully: " + response.body());
        } else {
            log.info("Failed to create user: " + response.body());
        }
    }
}
