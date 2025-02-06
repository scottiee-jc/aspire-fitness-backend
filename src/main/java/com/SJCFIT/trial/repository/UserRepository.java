package com.SJCFIT.trial.repository;

import com.SJCFIT.trial.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUser(String accountId);

    User findUserPreferences(String accountId);
}
