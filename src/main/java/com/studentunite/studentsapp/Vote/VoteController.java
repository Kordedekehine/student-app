package com.studentunite.studentsapp.Vote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/vote")
public class VoteController {
    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    // create
    @PostMapping
    public ResponseEntity<String> vote(@RequestBody VoteRequest voteRequest, @RequestParam String authorization){
        return voteService.vote(voteRequest, authorization);

    }
}