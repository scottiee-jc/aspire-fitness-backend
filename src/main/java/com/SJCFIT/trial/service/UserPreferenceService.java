package com.SJCFIT.trial.service;

import com.SJCFIT.trial.model.user.UserPreferences;
import org.springframework.stereotype.Service;

@Service
public interface UserPreferenceService {
    UserPreferences getUserPreferences(String accountId);

    UserPreferences createUserPreferences(String accountId);
}
