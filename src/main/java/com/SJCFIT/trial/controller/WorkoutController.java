package com.SJCFIT.trial.controller;

import com.SJCFIT.trial.model.exercise.Exercise;
import com.SJCFIT.trial.repository.ExerciseRepository;
import com.SJCFIT.trial.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/workouts")
public class WorkoutController {

    @Autowired
    ExerciseRepository exerciseRepository;
    @Autowired
    WorkoutService workoutService;

    @PostMapping("/createWorkout")
    public ResponseEntity<Exercise> createExercise(
            @RequestParam(name = "exerciseName") String exerciseName
            ){
        return null;
    }

    @GetMapping("/getAllWorkouts")
    public ResponseEntity<List<Exercise>> getAllExercises(){
        return ResponseEntity.ok(exerciseRepository.findAll());
    }

    @PostMapping("/saveWorkout")
    public ResponseEntity<String> saveWorkout(
            @RequestParam(name = "accountNumber") String accountNumber,
            @RequestParam(name = "query") String query
    ){
        // logic to create new workout object using a utility to construct obj from query
        // logic for calling workout service to save workout
        return new ResponseEntity<>( "Saved Successfully", HttpStatus.OK);
    }
}
