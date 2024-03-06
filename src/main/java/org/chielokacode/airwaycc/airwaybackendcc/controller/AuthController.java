package org.chielokacode.airwaycc.airwaybackendcc.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.chielokacode.airwaycc.airwaybackendcc.dto.*;
import org.chielokacode.airwaycc.airwaybackendcc.event.RegistrationCompleteEvent;
import org.chielokacode.airwaycc.airwaybackendcc.exception.InvalidTokenException;
import org.chielokacode.airwaycc.airwaybackendcc.exception.MailConnectionException;
import org.chielokacode.airwaycc.airwaybackendcc.exception.UserNotFoundException;
import org.chielokacode.airwaycc.airwaybackendcc.exception.UserNotVerifiedException;
import org.chielokacode.airwaycc.airwaybackendcc.model.User;
import org.chielokacode.airwaycc.airwaybackendcc.model.VerificationToken;
import org.chielokacode.airwaycc.airwaybackendcc.serviceImpl.EmailServiceImpl;
import org.chielokacode.airwaycc.airwaybackendcc.serviceImpl.FlightServiceImpl;
import org.chielokacode.airwaycc.airwaybackendcc.serviceImpl.UserServiceImpl;
import org.chielokacode.airwaycc.airwaybackendcc.utils.GoogleJwtUtils;
import org.chielokacode.airwaycc.airwaybackendcc.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/v1/user")
public class AuthController {
    private final UserServiceImpl userService;
    private final FlightServiceImpl flightService;

    private final ApplicationEventPublisher publisher;
    private JwtUtils jwtUtils;
    private final GoogleJwtUtils googleJwtUtils;

 private final EmailServiceImpl emailService;

    @Autowired
    public AuthController(ApplicationEventPublisher publisher, UserServiceImpl userService, FlightServiceImpl flightService, JwtUtils jwtUtils, GoogleJwtUtils googleJwtUtils, EmailServiceImpl emailService) {
        this.publisher = publisher;
        this.flightService = flightService;
        this.jwtUtils = jwtUtils;
        this.googleJwtUtils = googleJwtUtils;
        this.emailService = emailService;
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDto userDto,
                                                        final HttpServletRequest request) throws MailConnectionException {
        User user = userService.registerUser(userDto);
        try {
            publisher.publishEvent(new RegistrationCompleteEvent(
                    user,
                    emailService.applicationUrl(request)

            ));
        }catch (Exception e){
            throw new MailConnectionException("Error sending mail");
        }
        if(user != null) {
            return new ResponseEntity<>("Registration Successful", HttpStatus.OK);
        }else{
            throw new UserNotVerifiedException("Registration Unsuccessful");
        }

    }

    @GetMapping("/google/{token}")
    public ResponseEntity<String> AuthorizeOauthUser(@PathVariable String token){
        return ResponseEntity.ok(googleJwtUtils.googleOAuthUserJWT(token));
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto){
        String result = userService.logInUser(loginDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
        String result = userService.logoutUser(request);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("/verifyRegistration")
    public ResponseEntity<String> verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if(result.equalsIgnoreCase("valid")){
            return new ResponseEntity<>("User verified Successfully", HttpStatus.OK);
        }
        throw new UserNotVerifiedException("User is not Verified Successfully");
    }

    @GetMapping("/resendVerifyToken/{oldToken}")
    public ResponseEntity<String> resendVerificationToken(@PathVariable String oldToken,
                                                          HttpServletRequest request) throws MailConnectionException {

        VerificationToken verificationToken =
                userService.generateNewVerificationToken(oldToken);
        User user = verificationToken.getUser();
        try {
            emailService.resendVerificationTokenMail(user, emailService.applicationUrl(request), verificationToken);
        }catch (Exception e){
            throw new MailConnectionException("Error sending mail");
        }
        return new ResponseEntity<>("Verification Link Sent", HttpStatus.OK);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordDto passwordDto, HttpServletRequest request) throws MailConnectionException {
        User user = userService.findUserByEmail(passwordDto.getEmail());
        String url = "";
        if(user != null){
            String token =  flightService.generateRandomNumber(6);
            userService.createPasswordResetTokenForUser(user, token);
            try {
                url = emailService.passwordResetTokenMail(user, emailService.applicationUrl(request), token);
            }catch (Exception e){
                throw new MailConnectionException("Error sending mail");
            }
            return new ResponseEntity<>("Go to Email to reset Password " + url, HttpStatus.OK);
        }
        throw new UserNotFoundException("User with email " + passwordDto.getEmail() + "not found");
    }


    @PostMapping("/resetPassword/{token}")
    public ResponseEntity<String> resetPassword(@PathVariable String token,
                                                @RequestBody ResetPasswordDto passwordDto) {
        String result = userService.validatePasswordResetToken(token, passwordDto);
        if (!result.equalsIgnoreCase("valid")) {
            throw new InvalidTokenException("Invalid Token");
        }

        Optional<User> user = userService.getUserByPasswordReset(token);
        if (user.isPresent()) {
            userService.changePassword(user.get(), passwordDto.getNewPassword(), passwordDto.getNewConfirmPassword());
            return new ResponseEntity<>("Password Reset Successful", HttpStatus.OK);
        } else {
            throw new InvalidTokenException("Invalid Token");
        }
    }


    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDto changePasswordDto){
        String response = userService.changePasswordForUser(changePasswordDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}