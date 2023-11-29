package com.studentunite.studentsapp.login;

import com.studentunite.studentsapp.security.securityUtils.JWTToken;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginResponse {

    private String userName;

    private JWTToken jwtToken;
}
