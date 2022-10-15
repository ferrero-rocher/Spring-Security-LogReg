package com.example.springloginregistration.controller;

import com.example.springloginregistration.entity.UserEntity;
import com.example.springloginregistration.entity.VerificationURLEntity;
import com.example.springloginregistration.event.VerifyRegistrationEvent;
import com.example.springloginregistration.model.PasswordModel;
import com.example.springloginregistration.model.UserModel;
import com.example.springloginregistration.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request)
    {
        UserEntity user = userService.registerUser(userModel); // Register User in DB but initially he is not enabled
        // Create an event to store User in the DB as well as send email
        publisher.publishEvent(new VerifyRegistrationEvent(
                user,
                verificationURL(request)
        )); //here we publish the registered user and baseURL as part of the event

        return "Success";
    }

    //verify the newly registered user
    @GetMapping("/verifyRegistration")
    public String verifyUser(@RequestParam("token") String token)
    {
        String result = userService.validateVerificationToken(token);//check if the token received is legitimate
        if(result.equalsIgnoreCase("valid"))
        {
            return "Congratulations..!! you are now verified succcessfuly";
        }
        return "Validation failed";
    }
    //logic to send the verification URL
    @GetMapping("/resendVerificationURL")
    public String resendVerificationURL(@RequestParam("userid") String userid, final HttpServletRequest request)
    {
        VerificationURLEntity result = userService.findUser(Integer.parseInt(userid));//find verifiedURLEntity based on id
        if(result==null)
        {
            return "fail";
        }
        UserEntity user = result.getUser(); //extract the user

        resendVerificationURLMail(user,verificationURL(request),result.getToken());
        return "verification Link Sent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request)
    {
        UserEntity user = userService.findUserByEmail(passwordModel.getEmail());
        String url="";
        if(user!=null)
        {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user,token);
            url=passwordResendTokenMail(user,verificationURL(request),token);
        }
        return url;


    }

    @PostMapping("/savePassword")
    public String SavePassword(@RequestParam("token") String token , @RequestBody PasswordModel passwordModel)
    {
        String result = userService.validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase("valid"))
        {
            return "Invalid token";
        }
        Optional<UserEntity> user = userService.getUserByPasswordResetToken(token);
        if(user.isPresent())
        {
            userService.changePassword(user.get(),passwordModel.getNewPassword());
            return "Password reset succesfull";
        }
        else {
            return "invalid token";
        }

    }


    @PostMapping("/changePassword")
    public String  changePassword(@RequestBody PasswordModel passwordModel)
    {
        UserEntity user = userService.findUserByEmail(passwordModel.getEmail());
        if(!userService.checkIfValidPassword(user,passwordModel.getOldPassword()))
        {
            return "Invalid Old Password";
        }
        //Save new Password
        userService.changePassword(user,passwordModel.getNewPassword());
        return "Password changed succesfully";
    }













    private String passwordResendTokenMail(UserEntity user, String verificationURL, String token) {
        String url = verificationURL +"savePassword?token=" +token;

        log.info("Click link to reset your password :"+ url);
        return url;
    }


    //logic to form URL + send mail
    private void resendVerificationURLMail(UserEntity user, String verificationURL, String token) {
        String url = verificationURL +"verifyRegistration?token=" +token;

        log.info("Click link to verify your account :"+ url);
    }


    //Logic to create the BaseURL
    private String verificationURL(HttpServletRequest request) {

        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                "/" +
                request.getContextPath();
    }
}
