package com.studentunite.studentsapp.Forum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/forums")
public class ForumController {
    private final ForumService forumService;

    @Autowired
    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    // create
    @PostMapping
    public ResponseEntity<String> createForum(@RequestBody ForumRequest forumRequest,@RequestParam String authorization) {
        return forumService.createForum(forumRequest,authorization);
    }

    // read
    @GetMapping
    public ResponseEntity<List<ForumResponse>> getAllForums() {
        return ResponseEntity.ok(forumService.getAll());
    }

    @GetMapping("/{forumName}")
    public ResponseEntity<ForumResponse> getForumByName(@PathVariable String forumName) {
        return ResponseEntity.ok(forumService.getForumByName(forumName));
    }

    // update
    @PutMapping("/{forumName}")
    public ResponseEntity<String> updateForumDescription(@PathVariable String forumName,
                                                         @RequestBody String description,@RequestParam String authorization) {
        return forumService.updateForumDescription(forumName, description,authorization);
    }

    // delete
    @DeleteMapping("/{forumName}")
    public ResponseEntity<String> deleteForum(@PathVariable String forumName,@RequestParam String authorization) {
        return forumService.deleteForum(forumName,authorization);
    }
}
