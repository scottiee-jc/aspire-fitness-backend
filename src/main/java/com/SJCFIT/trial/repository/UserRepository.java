package com.SJCFIT.trial.repository;

import com.SJCFIT.trial.model.user.User;

import com.SJCFIT.trial.model.user.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUser(String accountId);
    UserPreferences findUserPreferences(String accountId);
}