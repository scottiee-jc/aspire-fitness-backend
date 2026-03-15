package com.SJCFIT.trial.service;

import com.SJCFIT.trial.model.user.UserPreferences;

public interface UserPreferenceService {
    UserPreferences getUserPreferences(String accountId);

    UserPreferences createUserPreferences(String accountId);
}
