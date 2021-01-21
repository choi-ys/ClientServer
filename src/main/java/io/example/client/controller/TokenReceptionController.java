package io.example.client.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.example.client.domain.AuthInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static io.example.client.util.MakeAuthoriztion.makeAuthorizationRequestHeader;

@Slf4j
@RestController
@RequestMapping(value = "/callback")
@RequiredArgsConstructor
public class TokenReceptionController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private AuthInfo authInfo;

    // http://localhost:9090/oauth/authorize?response_type=code&client_id=client&redirect_uri=http%3A%2F%2Flocalhost%3A9000%2Fcallback&scope=read_profile
    // http://localhost:9090/oauth/authorize?response_type=code&client_id=client&redirect_uri=http://localhost:9000/callback
    @GetMapping
    public void receptAuthorizationCode(@RequestParam(value = "code") String authorizationCode) throws JsonProcessingException {
        log.info("authorizationCode : {}", authorizationCode);
        String credentials = "client:secret";

        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic " + encodedCredentials);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", "http://localhost:9000/callback");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:9090/oauth/token", request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            AuthInfo authInfo = this.objectMapper.readValue(response.getBody(), AuthInfo.class);
            this.authInfo = authInfo;
            log.info(this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(authInfo));
        }else{
            log.error("Authorization Server Connection Error : [code : {}, detail : {}]", response.getStatusCode(), response.getBody());
        }
    }

    @GetMapping("/call")
    public void callResourceApi() throws JsonProcessingException {
        log.info(this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.authInfo));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + this.authInfo.getAccessToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        String url = "http://localhost:9090/api/test";
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        log.info(responseEntity.getBody());
    }

}
