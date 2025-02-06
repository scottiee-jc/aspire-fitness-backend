package com.SJCFIT.trial.implementations;

import com.SJCFIT.trial.model.User;
import com.SJCFIT.trial.model.UserPreferences;
import com.SJCFIT.trial.repository.UserRepository;
import com.SJCFIT.trial.service.UserPreferenceService;
import com.SJCFIT.trial.service.UserService;

public class UserPreferencesServiceImpl implements UserPreferenceService {

    private UserRepository userRepository;
    private UserService userService;

    @Override
    public UserPreferences getUserPreferences(String accountId) {
//        todo: fetch user preferences
        return null;
    }

    @Override
    public UserPreferences createUserPreferences(String accountId) {
        //        todo: create/update user preferences
        return null;
    }

    private String fetchUserSubscriberLevel(User user){
        boolean isSubscriber;
        //        todo: fetch subscriber level
        return null;
    }
}
