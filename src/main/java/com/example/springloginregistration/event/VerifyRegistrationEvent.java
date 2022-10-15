package com.example.springloginregistration.event;

import com.example.springloginregistration.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
//event class
public class VerifyRegistrationEvent extends ApplicationEvent {
    private final UserEntity user;
    private final String verificationUrl;


    public VerifyRegistrationEvent(UserEntity user, String verificationUrl) {
        super(user);
        this.user = user;
        this.verificationUrl=verificationUrl;

    }
}
