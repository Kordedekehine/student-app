package com.studentunite.studentsapp.Post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("api/posts")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    // create
    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody PostRequest postRequest,@RequestParam String authentication) {
        return postService.createPost(postRequest,authentication);
    }

    // read
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return status(HttpStatus.OK).body(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return status(HttpStatus.OK).body(postService.getPost(id));
    }

    @GetMapping("/forum/{forumName}")
    public ResponseEntity<List<PostResponse>> getPostsByForum(@PathVariable String forumName) {
        return status(HttpStatus.OK).body(postService.getPostsByForum(forumName));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<PostResponse>> getPostsByUsername(@PathVariable String username) {
        return status(HttpStatus.OK).body(postService.getPostsByUsername(username));
    }

    // update
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(@PathVariable Long id, @RequestBody String text,@RequestParam String authentication) {
        return postService.updatePost(id, text,authentication);
    }

    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id,@RequestParam String authentication) {
        return postService.deletePost(id,authentication);
    }
}
