package com.studentunite.studentsapp.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Registration")
public class RegisterController {

    private final RegistrationService registrationService;



    @Autowired
    public RegisterController(RegistrationService registrationService) {
        this.registrationService = registrationService;

    }

    @PostMapping("/signUp")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registrationRequest) {
        return registrationService.signUpUser(registrationRequest);
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestBody ValidateTokenRequest validateTokenRequest) {
        return registrationService.activateAccount(validateTokenRequest);
    }

}