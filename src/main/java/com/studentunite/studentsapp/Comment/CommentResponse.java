package com.studentunite.studentsapp.Comment;

import lombok.*;


@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long commentId;
    private Long postId;
    private String created;
    private String text;

}
