package com.studentunite.studentsapp.register;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class ValidateTokenRequest {

    @Email(message = "Invalid Mail")
    private String email;

    private String token;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
