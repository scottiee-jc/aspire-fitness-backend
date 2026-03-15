package com.SJCFIT.trial.controller;

import com.SJCFIT.trial.model.user.User;
import com.SJCFIT.trial.model.user.UserPreferences;
import com.SJCFIT.trial.repository.UserRepository;
import com.SJCFIT.trial.service.AuthenticationService;
import com.SJCFIT.trial.service.UserPreferenceService;
import com.SJCFIT.trial.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/user/v1")
public class UserController {

    private UserRepository userRepo;
    private UserPreferenceService userPreferenceService;
    private UserService userService;
    private AuthenticationService authenticationService;

    public UserController(UserRepository userRepo, UserPreferenceService userPreferenceService, UserService userService, AuthenticationService authenticationService) {
        this.userRepo = userRepo;
        this.userPreferenceService = userPreferenceService;
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(
            @RequestParam(name = "userId") String accountNumber,
            @RequestParam(name = "first_name") String firstName,
            @RequestParam(name = "first_name") String lastName,
            @RequestParam(name = "date_of_birth")LocalDate dob,
            Map<String, String> paramsMap
            ){
        User newUser = new User(firstName, lastName, dob);
        userRepo.save(newUser);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
    }

    @GetMapping("/getUser")
    public ResponseEntity<User> getUser(
            @RequestParam(name = "authenticationToken") String token
    ){
//        authenticationService.authenticateUser(token);
        return new ResponseEntity<>(userService.getUser(token), HttpStatus.OK);
    }

    @GetMapping("/getUserPreferences")
    public ResponseEntity<UserPreferences> getUserPreferences(
            @RequestParam(name = "account-id") String accountId
    ){
        UserPreferences userPreferences = userPreferenceService.getUserPreferences(accountId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(userPreferences);
    }

    @PostMapping("/saveWorkout")
    public ResponseEntity<String> saveWorkout(
            @RequestParam(name = "accountNumber") String accountNumber,
            @RequestParam(name = "query") String query
    ){
        // logic for user service to log workout
        return new ResponseEntity<>( "Saved Successfully", HttpStatus.OK);
    }

}
