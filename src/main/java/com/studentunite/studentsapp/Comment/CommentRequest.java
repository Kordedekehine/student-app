package com.studentunite.studentsapp.Comment;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {
    private Long postId;
    private String text;
}
