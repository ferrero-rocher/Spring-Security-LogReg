package com.example.springloginregistration.service;

import com.example.springloginregistration.entity.PasswordResetTokenEntity;
import com.example.springloginregistration.entity.UserEntity;
import com.example.springloginregistration.entity.VerificationURLEntity;
import com.example.springloginregistration.event.VerifyRegistrationEvent;
import com.example.springloginregistration.model.UserModel;
import com.example.springloginregistration.repository.PasswordResetTokenRepository;
import com.example.springloginregistration.repository.UserRepository;
import com.example.springloginregistration.repository.VerificationURLRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationURLRepository verificationURLRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;


    //logic to register the user in the DB
    @Override
    public UserEntity registerUser(UserModel userModel) {
        UserEntity user = new UserEntity();
        user.setEmail(userModel.getEmail());
        user.setFirstname(userModel.getFirstname());
        user.setLastname(userModel.getLastname());
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));//encrypting the password
        userRepository.save(user);
        return user;
    }
    //logic to insert token and userdetails in the Token entity
    @Override
    public void saveVerificationLinkForUser(UserEntity user,String url) {
        VerificationURLEntity verificationURL = new VerificationURLEntity(user,url);
        verificationURLRepository.save(verificationURL);

    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationURLEntity verificationToken=verificationURLRepository.findByToken(token);//Get the Verification Table object based on the token


        if(verificationToken == null)//no user found
        {
            return "invalid";
        }

        UserEntity user = verificationToken.getUser();//extract the userentity
        //logic to check if token is expired
        Calendar calendar = Calendar.getInstance();
        if(verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime() <=0)
        {
            return "Token expiered";
        }
        user.setIsenabled(true);//set the enabled field to true in the DB
        userRepository.save(user);//save the updated user
        return "valid";



    }

    @Override
    public VerificationURLEntity  findUser(int userid) {
        System.out.println("Fetching user");
        VerificationURLEntity registeredUser = verificationURLRepository.findByUserId(userid);
        System.out.println("User fetched" +registeredUser);
        if(registeredUser==null)
        {
            return null;
        }
        registeredUser.setToken(UUID.randomUUID().toString());
        verificationURLRepository.save(registeredUser);
        return registeredUser;
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(UserEntity user, String token) {
        PasswordResetTokenEntity passwordResetToken = new PasswordResetTokenEntity(user,token);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {

        PasswordResetTokenEntity passwordResetToken=passwordResetTokenRepository.findByToken(token);//Get the Verification Table object based on the token

        if(passwordResetToken == null)//no user found
        {
            return "invalid";
        }

        UserEntity user = passwordResetToken.getUser();//extract the userentity
        //logic to check if token is expired
        Calendar calendar = Calendar.getInstance();
        if(passwordResetToken.getExpirationTime().getTime() - calendar.getTime().getTime() <=0)
        {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "Token expiered";
        }

        return "valid";

    }

    @Override
    public Optional<UserEntity> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(UserEntity user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidPassword(UserEntity user, String oldPassword) {
        if(passwordEncoder.matches(oldPassword, user.getPassword()))
        {
            return true;
        }
        return false;
    }
}
