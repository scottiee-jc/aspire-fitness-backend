package com.SJCFIT.trial.repository;

import com.SJCFIT.trial.model.user.User;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUser(String accountId);

    User findUserPreferences(String accountId);
}
