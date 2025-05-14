package com.distopy.config;

import com.distopy.model.UserDtls;
import com.distopy.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Service
public class AuthSuccessHandlerImpl implements AuthenticationSuccessHandler{

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Set<String> roles = AuthorityUtils.authorityListToSet(authorities);

        if (authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            UserDtls userDtls = customUser.getUser();
            if (userDtls != null && userDtls.getId() != null) {
                userService.resetAttempt(userDtls.getId());
            }
        }

        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/");
        } else {
            response.sendRedirect("/");
        }
    }
}
