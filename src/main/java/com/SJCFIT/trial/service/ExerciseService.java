package com.SJCFIT.trial.service;

import com.SJCFIT.trial.model.Exercise;
import org.springframework.stereotype.Service;

@Service
public interface ExerciseService {
    void autoIncrementIdValue(Long id, Exercise exercise);
}
