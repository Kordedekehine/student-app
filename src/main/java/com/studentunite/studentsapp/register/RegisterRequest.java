package com.studentunite.studentsapp.register;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterRequest {

    private String username;
    private String email;
    private String password;
}
