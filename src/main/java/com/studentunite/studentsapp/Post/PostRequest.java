package com.studentunite.studentsapp.Post;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private String forumName;
    private String postName;
    private String text;
    private String image;
}
