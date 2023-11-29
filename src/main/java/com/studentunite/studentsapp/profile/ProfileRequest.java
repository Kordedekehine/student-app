package com.studentunite.studentsapp.profile;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProfileRequest {


    private String profilePicture;

    private String backgroundPicture;

    private String country;

    private String department;

    private String url;

    private String bio;
}
