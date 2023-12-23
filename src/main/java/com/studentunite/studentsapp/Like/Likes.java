package com.studentunite.studentsapp.Like;

import com.studentunite.studentsapp.AppUser.AppUser;
import com.studentunite.studentsapp.Comment.Comment;
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
public class Likes {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long likeId;


    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    @ToString.Exclude
    private Post post;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_id")
    @ToString.Exclude
    private Comment comment;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "app_user_id")
    @ToString.Exclude
    private AppUser appUser;



}
