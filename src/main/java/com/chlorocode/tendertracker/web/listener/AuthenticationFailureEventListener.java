package com.chlorocode.tendertracker.web.listener;

import com.chlorocode.tendertracker.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureEventListener
        implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private LoginAttemptService loginAttemptService;

    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) e.getAuthentication();
        String email = (String) auth.getPrincipal();

        loginAttemptService.loginFailed(email);
    }
}
