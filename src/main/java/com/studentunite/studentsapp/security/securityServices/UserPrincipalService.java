package com.studentunite.studentsapp.security.securityServices;

import com.studentunite.studentsapp.AppUser.AppUser;
import com.studentunite.studentsapp.AppUser.AppUserRepository;
import com.studentunite.studentsapp.login.LoginRequest;
import com.studentunite.studentsapp.security.securityUtils.JWTToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserPrincipalService implements UserDetailsService {
    @Autowired
    AppUserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private AppAuthenticationProvider authenticationManager;

    @Autowired
    TokenProviderServiceImpl tokenProviderService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<AppUser> optionalUser = userRepository.findByEmail(username);
        if(optionalUser.isEmpty()){
            throw new UsernameNotFoundException("User with given email not found");
        }
        else{
            AppUser user =  optionalUser.get();
            return ApplicationUser.create(user);
        }
    }


    public JWTToken loginUser(LoginRequest loginRequest) throws RuntimeException {
        Optional<AppUser> user = userRepository.findByUsername(loginRequest.getUsername());


        if(user.isPresent()){
            if(!user.get().isEnabled()){
                throw new RuntimeException("Account has not been enabled");
            }
            boolean matchingResult=passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword());

            if(!matchingResult){
                throw new RuntimeException("The password is Incorrect");
            }
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            user = userRepository.findByUsername(loginRequest.getUsername());

            JWTToken jwt = new JWTToken(tokenProviderService.generateLoginToken(authentication, user.get()));

            return jwt;
        }
        throw new UsernameNotFoundException("User Not Found");
    }

    public String signUpUser(AppUser user) {
        StringBuilder stringBuilder= new StringBuilder("Validates ");
        boolean userExists=userRepository.findByEmail(user.getEmail()).isPresent();
        if(userExists){
            throw new RuntimeException("user with this email already exists");
        }
        userRepository.save(user);
        String encodedPassword=passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        String token= UUID.randomUUID().toString();

        stringBuilder.append(token);
        return stringBuilder.toString();
    }

    public String sendRegistrationToken(AppUser user){
        //mailsender
        String token= UUID.randomUUID().toString().replace("-","").substring(0,4);
        return token;
    }


    public String getUserEmailAddressFromToken(String token) throws RuntimeException {
        return tokenProviderService.getEmailFromToken(token);
    }

    public boolean passwordMatches(String password,String password2){
       return passwordEncoder.matches(password, password2);
    }

}
