package com.studentunite.studentsapp.security.securityServices;

import com.studentunite.studentsapp.AppUser.AppUser;
import com.studentunite.studentsapp.AppUser.AppUserRepository;
import com.studentunite.studentsapp.AppUser.AppUserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Primary
public class AppAuthenticationProvider implements AuthenticationManager {

    @Autowired
    AppUserRepository userRepository;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        //String username = token.getName();
        String name = token.getName();
        String password = (String) token.getCredentials();



        Optional<AppUser> user = userRepository.findByUsername(name);

        if (user.isEmpty()) {
            throw new BadCredentialsException("There is no account with given credentials");
        }


        //UsersEntity usersEntity=user.get();
        AppUser appUser = user.get();
        List<AppUserRole> authorities = Collections.singletonList(appUser.getAppUserRole());
        if(appUser.getAppUserRole() == null) {
            try {
                throw new RuntimeException("User has no authority");
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return new UsernamePasswordAuthenticationToken(name, password, authorities.stream().map(authority
                -> new SimpleGrantedAuthority(authority.name())).collect(Collectors.toList()));
    }

}
