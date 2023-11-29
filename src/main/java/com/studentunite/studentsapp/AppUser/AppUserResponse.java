package com.studentunite.studentsapp.AppUser;

import com.studentunite.studentsapp.profile.ProfileResponse;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AppUserResponse {
    private String userName;
    private Integer numberOfPosts;
    private Integer numberOfComments;
    private String created;
    private ProfileResponse profileResponse;
}
