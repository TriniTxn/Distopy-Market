package com.distopy.config;

import com.distopy.model.UserDtls;
import com.distopy.repository.UserRepository;
import com.distopy.service.UserService;
import com.distopy.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("username");

        UserDtls userDtls = userRepository.findByEmail(email);

        if (userDtls != null) {

            if (userDtls.getIsEnabled()) {
                if (userDtls.getAccountNonLocked()) {
                    if (userDtls.getFailedLoginCount() < AppConstant.ATTEMPT_TIME) {
                        userService.increaseFailedLoginAttempts(userDtls);
                    } else {
                        userService.userAccountLock(userDtls);
                        exception = new LockedException("Your account is locked! You exceeded the maximum number of attempts.");
                    }

                } else {
                    if (userService.unlockAccountTimeExpired(userDtls)) {
                        exception = new LockedException("Your account is locked! Please login again.");
                    } else {
                        exception = new LockedException("Your account is locked! Try again in " + AppConstant.UNLOCK_DURATION_TIME / 60000 + " minutes.");
                    }
                }

            } else {
                exception = new LockedException("Your account is inactive!");
            }
        } else {
            exception = new LockedException("Email or Password is incorrect!");
        }
            super.setDefaultFailureUrl("/signin?error");
            super.onAuthenticationFailure(request, response, exception);
        }
    }
