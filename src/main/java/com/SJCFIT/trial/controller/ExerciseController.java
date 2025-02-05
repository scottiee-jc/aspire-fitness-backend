package com.SJCFIT.trial.controller;

import com.SJCFIT.trial.model.Exercise;
import com.SJCFIT.trial.repository.ExerciseRepository;
import com.SJCFIT.trial.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExerciseController{

    @Autowired
    ExerciseRepository exerciseRepository;
    @Autowired
    ExerciseService exerciseService;

    @PostMapping("/createExercise")
    public ResponseEntity<Exercise> createExercise(
            @RequestParam(name = "exerciseName") String exerciseName
            ){
        Exercise newExercise = new Exercise(exerciseName);
        exerciseRepository.save(newExercise);
        return ResponseEntity.ok(newExercise);
    }

    @GetMapping("/getAllExercises")
    public ResponseEntity<List<Exercise>> getAllExercises(){
        return ResponseEntity.ok(exerciseRepository.findAll());
    }

}
