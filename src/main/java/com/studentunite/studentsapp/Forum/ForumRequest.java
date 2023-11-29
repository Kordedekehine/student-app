package com.studentunite.studentsapp.Forum;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ForumRequest {
    private String forumName;
    private String description;
}
