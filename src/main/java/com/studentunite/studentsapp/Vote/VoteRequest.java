package com.studentunite.studentsapp.Vote;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VoteRequest {
    private VoteType voteType;
    private Long postId;
}
