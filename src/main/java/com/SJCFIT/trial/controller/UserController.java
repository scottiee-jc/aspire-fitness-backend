package com.SJCFIT.trial.controller;

import com.SJCFIT.trial.model.User;
import com.SJCFIT.trial.model.UserPreferences;
import com.SJCFIT.trial.repository.UserRepository;
import com.SJCFIT.trial.service.UserPreferenceService;
import com.SJCFIT.trial.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/user/v1")
public class UserController {

    private UserRepository userRepo;
    private UserPreferenceService userPreferenceService;

    @PostMapping("/createUser")
    public ResponseEntity<User> createUser(
            @RequestParam(name = "first_name") String firstName,
            @RequestParam(name = "first_name") String lastName,
            @RequestParam(name = "date_of_birth")LocalDate dob
            ){
        User newUser = new User(firstName, lastName, dob);
        userRepo.save(newUser);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
    }

    @GetMapping("/get-user-preferences")
    public ResponseEntity<UserPreferences> getUserPreferences(
            @RequestParam(name = "account-id") String accountId
    ){
        UserPreferences userPreferences = userPreferenceService.getUserPreferences(accountId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(userPreferences);
    }

}
