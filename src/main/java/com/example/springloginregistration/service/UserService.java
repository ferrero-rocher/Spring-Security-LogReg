package com.example.springloginregistration.service;

import com.example.springloginregistration.entity.UserEntity;
import com.example.springloginregistration.entity.VerificationURLEntity;
import com.example.springloginregistration.model.UserModel;

import java.util.Optional;

public interface UserService {
    UserEntity registerUser(UserModel userModel);

    void saveVerificationLinkForUser(UserEntity user,String url);


    String validateVerificationToken(String token);

    VerificationURLEntity findUser(int id);

    UserEntity findUserByEmail(String email);

    void createPasswordResetTokenForUser(UserEntity user, String token);

    String validatePasswordResetToken(String token);

    Optional<UserEntity> getUserByPasswordResetToken(String token);

    void changePassword(UserEntity user, String newPassword);

    boolean checkIfValidPassword(UserEntity user, String oldPassword);
}
