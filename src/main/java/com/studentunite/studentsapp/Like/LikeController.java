package com.studentunite.studentsapp.Like;

import com.studentunite.studentsapp.Vote.VoteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LikeController {

    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/likePost/{postId}")
    public ResponseEntity<String> likePost(@RequestBody Long postId, @RequestParam String authorization){
        return likeService.likePost(postId, authorization);

    }

    @DeleteMapping("/likePost/{postId}")
    public ResponseEntity<String> deletePost(@RequestBody Long postId, @RequestParam String authorization){
        return likeService.unlikePost(postId, authorization);

    }

    @PostMapping("/likeComm/{commentId}")
    public ResponseEntity<String> likeComment(@RequestBody Long commentId, @RequestParam String authorization){
        return likeService.likePost(commentId, authorization);
    }

    @DeleteMapping("/likeComm/{commentId}")
    public ResponseEntity<String> deleteComment(@RequestBody Long commentId, @RequestParam String authorization){
        return likeService.unlikePost(commentId, authorization);

    }

}
