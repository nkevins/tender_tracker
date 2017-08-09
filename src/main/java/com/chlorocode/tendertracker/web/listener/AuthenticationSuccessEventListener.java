package com.chlorocode.tendertracker.web.listener;

import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessEventListener
        implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private LoginAttemptService loginAttemptService;

    public void onApplicationEvent(AuthenticationSuccessEvent e) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) e.getAuthentication();
        CurrentUser user = (CurrentUser) auth.getPrincipal();

        loginAttemptService.loginSucceeded(user.getUsername());
    }
}