package com.studentunite.studentsapp.login;

import com.studentunite.studentsapp.AppUser.AppUser;
import com.studentunite.studentsapp.AppUser.AppUserRepository;
import com.studentunite.studentsapp.security.securityServices.UserPrincipalService;
import com.studentunite.studentsapp.security.securityUtils.JWTToken;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class LoginService {

    private final AppUserRepository appUserRepository;

    private final UserPrincipalService userPrincipalService;

    private final ModelMapper modelMapper;

    @Autowired
    public LoginService(AppUserRepository appUserRepository, UserPrincipalService userPrincipalService, ModelMapper modelMapper) {
        this.appUserRepository = appUserRepository;
        this.userPrincipalService = userPrincipalService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public LoginResponse loginUser(LoginRequest loginRequest) {

        JWTToken jwtToken = userPrincipalService.loginUser(loginRequest);

        Optional<AppUser> appUser = appUserRepository.findByUsername(loginRequest.getUsername());

        if (jwtToken != null) {

            if (appUser.isPresent()) {
                AppUser inAppUser = appUser.get();

                if (!inAppUser.isEnabled()) {
                    throw new RuntimeException("User not enabled");
                }
            }

            LoginResponse loginResponse = new LoginResponse();

            appUser.ifPresent(user -> modelMapper.map(user, loginResponse));

            loginResponse.setJwtToken(jwtToken);

            log.info(jwtToken.toString());

            return loginResponse;
        }

        throw new RuntimeException("Login Failed");

    }
}

