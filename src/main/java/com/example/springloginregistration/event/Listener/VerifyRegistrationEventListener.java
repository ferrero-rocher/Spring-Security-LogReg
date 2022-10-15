package com.example.springloginregistration.event.Listener;

import com.example.springloginregistration.entity.UserEntity;
import com.example.springloginregistration.event.VerifyRegistrationEvent;
import com.example.springloginregistration.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Slf4j
@Component

//listener for event that will subscribe to the publisher
public class VerifyRegistrationEventListener implements ApplicationListener<VerifyRegistrationEvent> {
    @Autowired
    private UserService userService;


    @Override
    public void onApplicationEvent(VerifyRegistrationEvent event) {

        UserEntity user = event.getUser(); // extracting the user from the event
        String token = UUID.randomUUID().toString();//logic to create random token
        userService.saveVerificationLinkForUser(user,token);//map user id and  UUID in db


        //send mail to user by concatenating the base url from the event and token generated from above step
        String url = event.getVerificationUrl() +"verifyRegistration?token=" +token;

        log.info("Click link to verify your account :"+ url);

    }
}
