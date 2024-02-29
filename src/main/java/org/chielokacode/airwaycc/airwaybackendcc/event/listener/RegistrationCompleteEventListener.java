package org.chielokacode.airwaycc.airwaybackendcc.event.listener;

import lombok.extern.slf4j.Slf4j;
import org.chielokacode.airwaycc.airwaybackendcc.event.RegistrationCompleteEvent;
import org.chielokacode.airwaycc.airwaybackendcc.model.User;
import org.chielokacode.airwaycc.airwaybackendcc.serviceImpl.EmailServiceImpl;
import org.chielokacode.airwaycc.airwaybackendcc.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


import java.util.UUID;
@Slf4j
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    private final EmailServiceImpl emailService;
    private final UserServiceImpl userService;
    @Autowired
    public RegistrationCompleteEventListener(EmailServiceImpl emailService, UserServiceImpl userService) {
        this.emailService = emailService;
        this.userService = userService;
    }
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        //Create the verification Token for the user with Link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(user, token);
        //Send Mail to User
        String url = event.getApplicationUrl() + "/api/v1/user/verifyRegistration?token=" + token;

        emailService.sendSimpleEmail(
                user.getEmail(),
                "Click on your verification link to verify your registration: " + url,
                "Verification Token Sent");

    }
}
