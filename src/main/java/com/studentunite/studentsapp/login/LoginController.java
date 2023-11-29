package com.studentunite.studentsapp.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/validate")
    public class LoginController {


        private final LoginService loginService;

   @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(loginService.loginUser(loginRequest), HttpStatus.ACCEPTED);
    }
}
