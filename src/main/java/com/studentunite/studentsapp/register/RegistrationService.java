package com.studentunite.studentsapp.register;

import com.studentunite.studentsapp.AppUser.AppUser;
import com.studentunite.studentsapp.AppUser.AppUserRepository;
import com.studentunite.studentsapp.AppUser.AppUserRole;
import com.studentunite.studentsapp.security.securityServices.TokenProviderService;
import com.studentunite.studentsapp.security.securityServices.UserPrincipalService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
public class RegistrationService {


    private final UserPrincipalService userPrincipalService;

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailValidator emailValidator;

    private final ModelMapper modelMapper;


    private final TokenProviderService tokenProviderService;

    public RegistrationService(UserPrincipalService userPrincipalService, AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, EmailValidator emailValidator, ModelMapper modelMapper, TokenProviderService tokenProviderService) {
        this.userPrincipalService = userPrincipalService;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailValidator = emailValidator;
        this.modelMapper = modelMapper;
        this.tokenProviderService = tokenProviderService;
    }

    @Transactional
    public ResponseEntity<String> signUpUser(RegisterRequest registerRequest){

        Optional<AppUser> userMailOptional = appUserRepository.findByEmail(registerRequest.getEmail());

        Optional<AppUser> userNameOptional = appUserRepository.findByUsername(registerRequest.getUsername());

        if (!emailValidator.isValid(registerRequest.getEmail())){
            return ResponseEntity.badRequest().body("Invalid Email Address");
        }

        if (userMailOptional.isPresent()){
            return ResponseEntity.badRequest().body("Email address already exists");
        }

        if (userNameOptional.isPresent()){
            return ResponseEntity.badRequest().body("Username already taken");
        }

        AppUser appUser = new AppUser();

        appUser.setEmail(registerRequest.getEmail());
        appUser.setUsername(registerRequest.getUsername());
        appUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        appUser.setAppUserRole(AppUserRole.USER);
        appUser.setCreated(Instant.now());
        appUser.setEnabled(false);

        String token = userPrincipalService.sendRegistrationToken(appUser);

        appUserRepository.save(appUser);

        appUser.setValidationToken(token);

        log.info(token);

      //  emailService.sendRegistrationSuccessfulEmail(appUser,token); //not ready to test

        return new ResponseEntity<>("Registration Successful. Activation email to be sent shortly.", HttpStatus.ACCEPTED);

    }

    @Transactional
    public ResponseEntity<String> activateAccount(ValidateTokenRequest validateTokenRequest){

     // String users =  tokenProviderService.getEmailFromToken(validateTokenRequest.getEmail());
        Optional<AppUser> users = appUserRepository.findByEmail(validateTokenRequest.getEmail());

      if (users.isEmpty()){
          throw new RuntimeException("Cannot retrieve token from mail");
      }

      AppUser appUser = users.get();

      if (appUser.getValidationToken().equals(validateTokenRequest.getToken())){
          log.info("its working");
          appUser.setEnabled(true);
          appUser.setValidationToken(null );

          appUserRepository.save(appUser);
      }
     // emailService.sendVerificationMessage(appUser);

      return new ResponseEntity<>("Account Activated!",HttpStatus.ACCEPTED);
    }


}
