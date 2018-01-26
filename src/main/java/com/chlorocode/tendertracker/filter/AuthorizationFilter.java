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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * This class is used to perform authentication & authorization filter check for all pages in the system.
 */
@Component
public class AuthorizationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        if (httpServletRequest.getRequestURI().contains("/admin")) {
            // Filter process for admin

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
                CurrentUser usr = (CurrentUser) auth.getPrincipal();

                if (usr.isNeedToSelectCompany()) {
                    ((HttpServletResponse)servletResponse).sendRedirect("/selectCompany");
                    return;
                }

                Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
                boolean isCompanyAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
                boolean isPreparer = authorities.contains(new SimpleGrantedAuthority("ROLE_PREPARER"));
                boolean isSubmitter = authorities.contains(new SimpleGrantedAuthority("ROLE_SUBMITTER"));
                boolean isSystemAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_SYS_ADMIN"));

                // Reject access if not Company Admin, System Admin, Submitter, or Preparer
                if (!isCompanyAdmin && !isSystemAdmin && !isPreparer && !isSubmitter) {
                    ((HttpServletResponse)servletResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                // No authentication detected
                ((HttpServletResponse)servletResponse).sendRedirect("/login");
            }
        } else {
            // Filter process for other page

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
                CurrentUser usr = (CurrentUser) auth.getPrincipal();

                if (usr.isNeedToSelectCompany() && !httpServletRequest.getRequestURI().equals("/selectCompany") && !httpServletRequest.getRequestURI().contains("/assets/")) {
                    ((HttpServletResponse)servletResponse).sendRedirect("/selectCompany");
                    return;
                } else {
                    filterChain.doFilter(servletRequest, servletResponse);
                }
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }
}
