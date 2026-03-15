package com.SJCFIT.trial.controller;

import com.SJCFIT.trial.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/authz/v1")
public class AuthenticationController {

    private AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("login")
    public ResponseEntity<String> login (
            @RequestParam(name = "username") String username,
            @RequestParam(name = "password") String password
    ){
        return new ResponseEntity<>(authenticationService.authenticateUser(username, password), HttpStatus.OK);
    }

}
