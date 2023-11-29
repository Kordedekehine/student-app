package com.studentunite.studentsapp.Vote;

import com.studentunite.studentsapp.AppUser.AppUser;
import com.studentunite.studentsapp.Post.Post;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;


@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long voteId;

    private VoteType voteType;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    @ToString.Exclude
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "app_user_id")
    @ToString.Exclude
    private AppUser appUser;
}