package com.studentunite.studentsapp.Vote;

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
public class VoteService {
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final UserPrincipalService userPrincipalService;


    @Autowired
    public VoteService(VoteRepository voteRepository, PostRepository postRepository, UserPrincipalService userPrincipalService) {
        this.voteRepository = voteRepository;
        this.postRepository = postRepository;
        this.userPrincipalService = userPrincipalService;
    }

    public String checkIfUserAuthorized(String authorization){

        String userEmail= userPrincipalService.getUserEmailAddressFromToken(authorization);

        if (userEmail.isEmpty()){

            throw new RuntimeException("User does not exist");
        }

        return userEmail;
    }

    @Transactional
    public ResponseEntity<String> vote(VoteRequest voteRequest,String authorization) {

        checkIfUserAuthorized(authorization);

        Optional<Post> postOptional = postRepository.findById(voteRequest.getPostId());
        if (postOptional.isEmpty())
            return ResponseEntity.badRequest().body("Post with ID " + voteRequest.getPostId() + " not found");

        Post post = postOptional.get();

        Optional<Vote> voteByPost = voteRepository.findByPost(post);

        if (voteByPost.isPresent()) {
            // If vote is found, update it
            Vote vote = voteByPost.get();
            // remove vote tally from post --neutralize the current count
            post.setVoteCount(post.getVoteCount() - vote.getVoteType().getDirection());

            //double voting not allowed remove it
            if (vote.getVoteType().equals(voteRequest.getVoteType())) {
                voteRepository.delete(vote);
            } else {
                // if the vote is different, update it
                vote.setVoteType(voteRequest.getVoteType());
                voteRepository.save(vote);
                //Now add vote tally to post
                post.setVoteCount(post.getVoteCount() + voteRequest.getVoteType().getDirection());
            }
        } else {
            // Otherwise, if the vote is not found, create it
            voteRepository.save(mapRequestToVote(voteRequest, post));
            // add vote tally to post-- I hope you know zero plus one still one so no P
            post.setVoteCount(post.getVoteCount() + voteRequest.getVoteType().getDirection());
        }

        postRepository.save(post);
        return new ResponseEntity<>("Vote cast", HttpStatus.OK);
    }


    private Vote mapRequestToVote(VoteRequest voteRequest, Post post) {
        return Vote.builder()
                .voteType(voteRequest.getVoteType())
                .post(post)
                .build();
    }
}
