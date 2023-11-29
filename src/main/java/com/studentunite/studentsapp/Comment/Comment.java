package com.studentunite.studentsapp.Comment;

import com.studentunite.studentsapp.AppUser.AppUser;
import com.studentunite.studentsapp.Post.Post;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long commentId;

    @NotEmpty(message = "Comment is required")
    @Lob    // Large object
    private String text;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    @ToString.Exclude
    private Post post;

    private Instant createdDate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "app_user_id")
    @ToString.Exclude
    private AppUser appUser;

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
}