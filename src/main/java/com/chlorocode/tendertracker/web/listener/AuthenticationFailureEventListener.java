package com.chlorocode.tendertracker.web.listener;

import com.chlorocode.tendertracker.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

/**
 * This class is used to intercept the authentication login failure process.
 */
@Component
public class AuthenticationFailureEventListener
        implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private LoginAttemptService loginAttemptService;

    /**
     * This method will be fired when user's authentication process is fail.
     * When authentication is failure, application will increase the attempt count of user to Max.
     * If maximum amount of attempt is exceed, application will lock the user account.
     *
     * @param e AuthenticationFailureBadCredentialsEvent
     */
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) e.getAuthentication();
        String email = (String) auth.getPrincipal();

        loginAttemptService.loginFailed(email);
    }
}
