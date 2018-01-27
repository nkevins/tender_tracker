package com.chlorocode.tendertracker.web.listener;

import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * This class is used to intercept the authentication login success process.
 */
@Component
public class AuthenticationSuccessEventListener
        implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private LoginAttemptService loginAttemptService;

    /**
     * This method will be fired when user's authentication process is success.
     * When authentication is success, application will reset the attempt count of user account to 0.
     *
     * @param e AuthenticationSuccessEvent
     */
    public void onApplicationEvent(AuthenticationSuccessEvent e) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) e.getAuthentication();
        CurrentUser user = (CurrentUser) auth.getPrincipal();

        loginAttemptService.loginSucceeded(user.getUsername());
    }
}