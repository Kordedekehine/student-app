package com.studentunite.studentsapp.Post;

import com.studentunite.studentsapp.Comment.CommentRepository;
import com.studentunite.studentsapp.Forum.Forum;
import com.studentunite.studentsapp.Forum.ForumRepository;
import com.studentunite.studentsapp.Utils.Utils;
import com.studentunite.studentsapp.Vote.Vote;
import com.studentunite.studentsapp.Vote.VoteRepository;
import com.studentunite.studentsapp.Vote.VoteType;
import com.studentunite.studentsapp.security.securityServices.UserPrincipalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final ForumRepository forumRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;

    private final UserPrincipalService userPrincipalService;

    @Autowired
    public PostService(PostRepository postRepository, ForumRepository forumRepository, CommentRepository commentRepository, VoteRepository voteRepository, UserPrincipalService userPrincipalService) {
        this.postRepository = postRepository;
        this.forumRepository = forumRepository;
        this.commentRepository = commentRepository;
        this.voteRepository = voteRepository;
        this.userPrincipalService = userPrincipalService;
    }

        @Transactional
        public ResponseEntity<String> createPost(PostRequest postRequest,String authentication) {

        checkIfUserAuthorized(authentication);
        //  check if forum exists
        Optional<Forum> forumOptional = forumRepository.findByForumName(postRequest.getForumName());
        if (forumOptional.isEmpty())
            return new ResponseEntity<>("Forum not found", HttpStatus.NOT_FOUND);

        // check for empty post titles
        if (postRequest.getPostName().length() < 1)
            return new ResponseEntity<>("Post name cannot be empty", HttpStatus.BAD_REQUEST);

        // check if post name is too long
        if (postRequest.getPostName().length() > 256)
            return new ResponseEntity<>("Post title cannot be longer than 256 characters", HttpStatus.BAD_REQUEST);

        // check if image is too large
        if (postRequest.getImage() != null && postRequest.getImage().getBytes(StandardCharsets.UTF_8).length > 1000000)
            return new ResponseEntity<>("Image cannot be larger than 1MB", HttpStatus.BAD_REQUEST);

        postRepository.save(mapRequestToPost(postRequest, forumOptional.get()));
        return new ResponseEntity<>("Post created", HttpStatus.CREATED);
    }

    public String checkIfUserAuthorized(String authorization){

        String userEmail= userPrincipalService.getUserEmailAddressFromToken(authorization);

        if (userEmail.isEmpty()){

            throw new RuntimeException("User does not exist");
        }

        return userEmail;
    }

    private Post mapRequestToPost(PostRequest postRequest, Forum forum) {
        return Post.builder()
                .postName(postRequest.getPostName())
                .text(postRequest.getText())
                .image(postRequest.getImage().getBytes())
                .voteCount(0)
                .createdDate(Instant.now())
                .forum(forum)
                .build();
    }

    private PostResponse mapPostToResponse(Post post) {
        Vote vote = voteRepository.findByPost(post).orElse(null);
        return PostResponse.builder()
                .postId(post.getPostId())
                .postName(post.getPostName())
                .text(post.getText())
                .image(new String(post.getImage()))
                .forumName(post.getForum().getForumName())
                .voteCount(post.getVoteCount())
                .commentCount(commentRepository.findAllByPost(post).size())
                .created(Utils.timeAgo(post.getCreatedDate()))
                .upVote(vote != null && vote.getVoteType().equals(VoteType.UPVOTE))
                .downVote(vote != null && vote.getVoteType().equals(VoteType.DOWNVOTE))
                .build();
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post with id " + id.toString() + " not found"));
        return mapPostToResponse(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        posts.sort((p1, p2) -> p2.getVoteCount().compareTo(p1.getVoteCount()));     //sort by votes
        return posts.stream().map(this::mapPostToResponse).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByForum(String forumName) {
        List<Post> posts = postRepository.findAllByForumForumName(forumName);
        if (posts.isEmpty()){
            throw new RuntimeException("Forum does not exist");
        }
        posts.sort((p1, p2) -> p2.getVoteCount().compareTo(p1.getVoteCount()));     //sort by votes
        return posts.stream().map(this::mapPostToResponse).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        List<Post> posts = postRepository.findAllByAppUserUsername(username);

        if (posts.isEmpty()){
            throw new RuntimeException("Such User does not exist");
        }
        posts.sort((p1, p2) -> p2.getVoteCount().compareTo(p1.getVoteCount()));     //sort by votes
        return posts.stream().map(this::mapPostToResponse).collect(toList());
    }

    @Transactional
    public ResponseEntity<String> updatePost(Long id, String text,String authorization) {

        checkIfUserAuthorized(authorization);
        Optional<Post> postOptional = postRepository.findById(id);

        // check if exists
        if (postOptional.isEmpty())
            return new ResponseEntity<>("Post with id " + id.toString() + " not found", HttpStatus.NOT_FOUND);

        Post post = postOptional.get();

        post.setText(text);
        postRepository.save(post);
        return new ResponseEntity<>("Post updated", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> deletePost(Long id, String authentication) {

        checkIfUserAuthorized(authentication);

        Optional<Post> postOptional = postRepository.findById(id);

        // check if exists
        if (postOptional.isEmpty())
            return new ResponseEntity<>("Post with id " + id.toString() + " not found", HttpStatus.NOT_FOUND);

        Post post = postOptional.get();

        // delete all comments in this post
        commentRepository.deleteAllByPost(post);

        // delete all votes in this post
        voteRepository.deleteAllByPost(post);

        // delete post
        postRepository.delete(post);

        return new ResponseEntity<>("Post deleted", HttpStatus.OK);
    }
}
