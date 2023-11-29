package com.studentunite.studentsapp.Post;

import com.studentunite.studentsapp.AppUser.AppUser;
import com.studentunite.studentsapp.Forum.Forum;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;


@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long postId;

    @NotBlank(message = "Post Name cannot be empty or Null")
    private String postName;

    @Lob    // Large object
    private String text;

    @Column(nullable = false, length = 5000000) // 5MB
    private byte[] image;

    private Integer voteCount = 0;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "app_user_id")
    @ToString.Exclude
    private AppUser appUser;

    private Instant createdDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "forum_id")
    @ToString.Exclude
    private Forum forum;
}
