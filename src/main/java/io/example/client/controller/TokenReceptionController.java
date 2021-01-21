package io.example.client.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping(value = "/callback", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value = "/callback")
@Slf4j
public class TokenReceptionController {

    @GetMapping
    public void receptAuthorizationCode(@RequestParam(value = "code") String authorizationCode){
        log.info("authorizationCode : {}", authorizationCode);
    }
}
