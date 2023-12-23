package com.studentunite.studentsapp.Like;

import com.studentunite.studentsapp.AppUser.AppUser;
import com.studentunite.studentsapp.AppUser.AppUserRepository;
import com.studentunite.studentsapp.Comment.Comment;
import com.studentunite.studentsapp.Comment.CommentRepository;
import com.studentunite.studentsapp.Post.Post;
import com.studentunite.studentsapp.Post.PostRepository;
import com.studentunite.studentsapp.security.securityServices.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;


    private final AppUserRepository appUserRepository;


    private final PostRepository postRepository;


    private final CommentRepository commentRepository;


    private final UserPrincipalService userPrincipalService;

    @Autowired
    public LikeService(LikeRepository likeRepository, AppUserRepository appUserRepository, PostRepository postRepository, CommentRepository commentRepository, UserPrincipalService userPrincipalService) {
        this.likeRepository = likeRepository;
        this.appUserRepository = appUserRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userPrincipalService = userPrincipalService;
    }


    @Transactional
    public ResponseEntity<String> likePost(Long postId, String authentication){

      String user =  checkIfUserAuthorized(authentication);

        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()){
            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
        }

        Post post = optionalPost.get();

        Optional<AppUser> optionalAppUser = appUserRepository.findByUsername(user);
        if (optionalAppUser.isEmpty()){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        AppUser appUser = optionalAppUser.get();

        if (likeRepository.existsByPostAndAppUser(post,appUser)){
            return new ResponseEntity<>("User liked the post already", HttpStatus.CONFLICT);
        }

        Likes like = new Likes();
        like.setPost(post);
        like.setAppUser(appUser);
        likeRepository.save(like);

        if (post.getLikeCount() == null) {
            post.setLikeCount(0);
        }
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);

        return new ResponseEntity<>("You like the post", HttpStatus.OK);

    }


    @Transactional
    public ResponseEntity<String> likeComments(Long commentId, String authentication){

        String user =  checkIfUserAuthorized(authentication);

        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()){
            return new ResponseEntity<>("Comment not found", HttpStatus.NOT_FOUND);
        }

        Comment comment = optionalComment.get();

        Optional<AppUser> optionalAppUser = appUserRepository.findByUsername(user);
        if (optionalAppUser.isEmpty()){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        AppUser appUser = optionalAppUser.get();

        if (likeRepository.existsByPostAndAppUser(comment,appUser)){
            return new ResponseEntity<>("User liked the comment already", HttpStatus.CONFLICT);
        }

        Likes like = new Likes();
        like.setComment(comment);
        like.setAppUser(appUser);
        likeRepository.save(like);

        if (comment.getLikeCount() == null) {
            comment.setLikeCount(0);
        }
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);

        return new ResponseEntity<>("You like the comment!", HttpStatus.OK);

    }


    @Transactional
    public ResponseEntity<String> unlikeComments(Long commentId, String authentication){

        String user =  checkIfUserAuthorized(authentication);

        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()){
            return new ResponseEntity<>("Comment not found", HttpStatus.NOT_FOUND);
        }

        Comment comment = optionalComment.get();

        Optional<AppUser> optionalAppUser = appUserRepository.findByUsername(user);
        if (optionalAppUser.isEmpty()){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        AppUser appUser = optionalAppUser.get();

        Likes like = likeRepository.findByCommentAndAppUser(comment,appUser);
        if (like == null) {
            return new ResponseEntity<>("Cannot Unlike because you're yet to like", HttpStatus.CONFLICT);
        }


        likeRepository.delete(like);


        comment.setLikeCount(comment.getLikeCount() - 1);
        commentRepository.save(comment);

        return new ResponseEntity<>("You unlike the comment!", HttpStatus.OK);

    }


    @Transactional
    public ResponseEntity<String> unlikePost(Long postId, String authentication){

        String user =  checkIfUserAuthorized(authentication);

        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()){
            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
        }

        Post post = optionalPost.get();

        Optional<AppUser> optionalAppUser = appUserRepository.findByUsername(user);
        if (optionalAppUser.isEmpty()){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        AppUser appUser = optionalAppUser.get();

        Likes like = likeRepository.findByPostAndAppUser(post,appUser);
        if (like == null){
            return new ResponseEntity<>("Cannot Unlike because you're yet to like", HttpStatus.CONFLICT);
        }


        likeRepository.delete(like);

        post.setLikeCount(post.getLikeCount() - 1);
        postRepository.save(post);

        return new ResponseEntity<>("You unlike the post", HttpStatus.OK);

    }



    public String checkIfUserAuthorized(String authorization){

        String userEmail= userPrincipalService.getUserEmailAddressFromToken(authorization);

        if (userEmail.isEmpty()){

            throw new RuntimeException("User does not exist");
        }

        return userEmail;
    }

}
