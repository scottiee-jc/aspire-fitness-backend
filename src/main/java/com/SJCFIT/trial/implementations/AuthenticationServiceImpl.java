package com.SJCFIT.trial.implementations;

import com.SJCFIT.trial.service.AuthenticationService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public String authenticateUser(String username, String password) {
        // TODO: implement real authentication logic (e.g. JWT token generation)
        return "authenticated";
    }
}

