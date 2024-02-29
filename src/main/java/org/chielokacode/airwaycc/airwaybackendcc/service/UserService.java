package org.chielokacode.airwaycc.airwaybackendcc.service;


import jakarta.servlet.http.HttpServletRequest;
import org.chielokacode.airwaycc.airwaybackendcc.dto.ChangePasswordRequestDto;
import org.chielokacode.airwaycc.airwaybackendcc.dto.LoginDto;
import org.chielokacode.airwaycc.airwaybackendcc.dto.ResetPasswordDto;
import org.chielokacode.airwaycc.airwaybackendcc.model.User;
import org.chielokacode.airwaycc.airwaybackendcc.model.VerificationToken;


import java.util.Optional;

public interface UserService {
    String logoutUser(HttpServletRequest request);
    void saveVerificationTokenForUser(User user, String token);
    String logInUser(LoginDto userDto);
    String validateVerificationToken(String token);
    VerificationToken generateNewVerificationToken(String oldToken);
    void createPasswordResetTokenForUser(User user, String token);
    User findUserByEmail(String email);
    String validatePasswordResetToken(String token, ResetPasswordDto passwordDto);
    Optional<User> getUserByPasswordReset(String token);
    void changePassword(User user, String newPassword, String newConfirmPassword);

    String changePasswordForUser(ChangePasswordRequestDto changePasswordDto);
}
