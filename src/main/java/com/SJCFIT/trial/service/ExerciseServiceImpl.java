package com.SJCFIT.trial.service;

import com.SJCFIT.trial.model.Exercise;
import com.SJCFIT.trial.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExerciseServiceImpl implements ExerciseService {
    @Autowired
    ExerciseRepository exerciseRepository;
    @Override
    public void autoIncrementIdValue(Long id, Exercise exercise) {
//        int i = 0;
//        i = exerciseRepository.findAll().size();
//        id = Long.valueOf(i+1);
//        exercise.setId(id);
//        exerciseRepository.save(exercise);
    }
}
