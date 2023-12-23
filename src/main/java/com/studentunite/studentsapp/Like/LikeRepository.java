package com.studentunite.studentsapp.Like;

import com.studentunite.studentsapp.AppUser.AppUser;
import com.studentunite.studentsapp.Comment.Comment;
import com.studentunite.studentsapp.Post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Likes,Likes> {

    boolean existsByPostAndAppUser(Post post, AppUser user);

    boolean existsByPostAndAppUser(Comment comment, AppUser user);

    Likes findByPostAndAppUser(Post post, AppUser user);

    Likes findByCommentAndAppUser(Comment comment, AppUser user);

}
