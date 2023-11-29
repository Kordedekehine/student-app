package com.studentunite.studentsapp.profile;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProfileResponse {

    private String profilePicture;

    private String backgroundPicture;

    private String username;

    private String country;

    private String department;

    private String url;

    private String bio;

}
