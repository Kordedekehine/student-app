package com.studentunite.studentsapp.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByForumForumName(String forumName);
    List<Post> findAllByAppUserUsername(String username);
}
