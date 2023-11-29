package com.studentunite.studentsapp.Comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService CommentService;

    @Autowired
    public CommentController(CommentService commentService) {
        CommentService = commentService;
    }

    // create
    @PostMapping
    public ResponseEntity<String> createComment(@RequestBody CommentRequest CommentRequest,@RequestParam String authorization) {
        return CommentService.createComment(CommentRequest,authorization);
    }

    // read

    @GetMapping("/all")
    public ResponseEntity<List<CommentResponse>> getAllComments() {
        return new ResponseEntity<>(CommentService.getAllComments(),HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable Long id) {
        return status(HttpStatus.OK).body(CommentService.getComment(id));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(@PathVariable Long postId) {
        return status(HttpStatus.OK).body(CommentService.getCommentsByPost(postId));
    }


    // update
    @PutMapping("/{id}")
    public ResponseEntity<String> updateComment(@PathVariable Long id, @RequestBody String text,@RequestParam String authorization) {
        return CommentService.updateComment(id, text,authorization);
    }

    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id,@RequestParam String authorization) {
        return CommentService.deleteComment(id,authorization);
    }
}
