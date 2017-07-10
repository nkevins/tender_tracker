package com.chlorocode.tendertracker.filter;

import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
public class AdminFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            CurrentUser usr = (CurrentUser) auth.getPrincipal();

            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            boolean isCompanyAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
            boolean isSystemAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_SYS_ADMIN"));

            // Reject access if not Company Admin and System Admin
            if (!isCompanyAdmin && !isSystemAdmin) {
                ((HttpServletResponse)servletResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // If company admin hasn't select company to be managed
            if (isCompanyAdmin && usr.getSelectedCompany() == null) {
                ((HttpServletResponse)servletResponse).sendRedirect("/selectCompany");
                return;
            }

            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            // No authentication detected
            ((HttpServletResponse)servletResponse).sendRedirect("/login");
        }
    }
}
