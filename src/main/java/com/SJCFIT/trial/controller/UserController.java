package com.SJCFIT.trial.controller;

import com.SJCFIT.trial.model.User;
import com.SJCFIT.trial.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

}
