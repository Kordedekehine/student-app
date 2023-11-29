package com.studentunite.studentsapp.Comment;

import com.studentunite.studentsapp.AppUser.AppUser;
import com.studentunite.studentsapp.AppUser.AppUserRepository;
import com.studentunite.studentsapp.Post.Post;
import com.studentunite.studentsapp.Post.PostRepository;
import com.studentunite.studentsapp.Utils.Utils;
import com.studentunite.studentsapp.security.securityServices.UserPrincipalService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final UserPrincipalService userPrincipalService;

    private final ModelMapper modelMapper;

    private final AppUserRepository appUserRepository;

    @Autowired
    public CommentService(CommentRepository CommentRepository, PostRepository postRepository, UserPrincipalService userPrincipalService, ModelMapper modelMapper, AppUserRepository appUserRepository) {
        this.commentRepository = CommentRepository;
        this.postRepository = postRepository;
        this.userPrincipalService = userPrincipalService;
        this.modelMapper = modelMapper;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public ResponseEntity<String> createComment(CommentRequest CommentRequest,String authorization) {

        checkIfUserAuthorized(authorization);

        Optional<Post> postOptional = postRepository.findById(CommentRequest.getPostId());
        if (postOptional.isEmpty())
            return new ResponseEntity<>("Post \"" + CommentRequest.getPostId() + "\" does not exist", HttpStatus.NOT_FOUND);

        if (CommentRequest.getText().length() < 1)
            return new ResponseEntity<>("Text cannot be empty", HttpStatus.BAD_REQUEST);

        commentRepository.save(mapRequestToComment(CommentRequest, postOptional.get()));
        return new ResponseEntity<>("Comment created", HttpStatus.CREATED);
    }

    public AppUser getAppUserByUsername(String username) {
        return appUserRepository.findByUsername(username).orElse(null);
    }

    private Comment mapRequestToComment(CommentRequest CommentRequest, Post post) {
        return Comment.builder()
                .text(CommentRequest.getText())
                .createdDate(Instant.now())
                .post(post)
                .build();
    }

    private CommentResponse mapCommentToResponse(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .text(comment.getText())
                .postId(comment.getPost().getPostId())
             //   .username(comment.getAppUser().getUsername())
                .created(Utils.timeAgo(comment.getCreatedDate()))
                .build();
    }

    @Transactional
    public CommentResponse getComment(Long id){

        Optional<Comment> comment = commentRepository.findById(id);

        if (comment.isEmpty()){
            throw new RuntimeException("Comment with id " + id.toString() + " not found");
        }
        return mapCommentToResponse(comment.get());
    }

    public String checkIfUserAuthorized(String authorization){

        String userEmail= userPrincipalService.getUserEmailAddressFromToken(authorization);

        if (userEmail.isEmpty()){

            throw new RuntimeException("User does not exist");
        }

       return userEmail;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getAllComments() {

        List<Comment> comments = commentRepository.findAll();
        //sort by created date
        comments.sort((c1, c2) -> c2.getCreatedDate().compareTo(c1.getCreatedDate()));
        return comments.stream().map(this::mapCommentToResponse).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPost(Long postId) {

        List<Comment> comments = commentRepository.findAllByPostPostId(postId);
        //sort by created date
        comments.sort((c1, c2) -> c2.getCreatedDate().compareTo(c1.getCreatedDate()));
        return comments.stream().map(this::mapCommentToResponse).collect(toList());
    }

    @Transactional
    public ResponseEntity<String> updateComment(Long id, String text,String authentication) {

        checkIfUserAuthorized(authentication);

        Optional<Comment> commentOptional = commentRepository.findById(id);

        // check if exists
        if (commentOptional.isEmpty())
            return new ResponseEntity<>("Comment with id" + id.toString() + " not found", HttpStatus.NOT_FOUND);

        Comment Comment = commentOptional.get();

        Comment.setText(text);
        commentRepository.save(Comment);
        return new ResponseEntity<>("Comment updated", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> deleteComment(Long id,String authorization) {

        checkIfUserAuthorized(authorization);

        Optional<Comment> commentOptional = commentRepository.findById(id);

        // check if exists
        if (commentOptional.isEmpty())
            return new ResponseEntity<>("Comment with id" + id.toString() + " not found", HttpStatus.NOT_FOUND);

        Comment Comment = commentOptional.get();

        commentRepository.delete(Comment);
        return new ResponseEntity<>("Comment deleted", HttpStatus.OK);
    }
}
