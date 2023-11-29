package com.studentunite.studentsapp.Forum;

import com.studentunite.studentsapp.Post.Post;
import com.studentunite.studentsapp.Post.PostRepository;
import com.studentunite.studentsapp.Post.PostService;
import com.studentunite.studentsapp.Utils.Utils;
import com.studentunite.studentsapp.security.securityServices.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ForumService {
    private final ForumRepository forumRepository;
    private final PostRepository postRepository;

    private final PostService postService;

    private final UserPrincipalService userPrincipalService;


    @Autowired
    public ForumService(ForumRepository forumRepository, PostRepository postRepository, PostService postService, UserPrincipalService userPrincipalService) {
        this.forumRepository = forumRepository;
        this.postRepository = postRepository;
        this.postService = postService;
        this.userPrincipalService = userPrincipalService;
    }

    @Transactional
    public ResponseEntity<String> createForum(ForumRequest forumRequest,String authorization) {

        checkIfUserAuthorized(authorization);
        // check if forum already exists
        if (forumRepository.findByForumName(forumRequest.getForumName()).isPresent())
            return new ResponseEntity<>("Forum with name " + forumRequest.getForumName() + " already exists", HttpStatus.BAD_REQUEST);

        // check if forum name is empty
        if (forumRequest.getForumName().length() < 1)
            return new ResponseEntity<>("Forum name cannot be empty", HttpStatus.BAD_REQUEST);

        // check if forum name is too long
        if (forumRequest.getForumName().length() > 32)
            return new ResponseEntity<>("Forum name cannot be longer than 32 characters", HttpStatus.BAD_REQUEST);

        // check if forum description is empty
        if (forumRequest.getDescription().length() < 1)
            return new ResponseEntity<>("Forum description cannot be empty", HttpStatus.BAD_REQUEST);

        Forum forum = mapRequestToForum(forumRequest);
        forumRepository.save(forum);
        return new ResponseEntity<>("Forum created", HttpStatus.CREATED);
    }

    public String checkIfUserAuthorized(String authorization){

        String userEmail= userPrincipalService.getUserEmailAddressFromToken(authorization);

        if (userEmail.isEmpty()){

            throw new RuntimeException("User does not exist");
        }
        return userEmail;
    }

    private Forum mapRequestToForum(ForumRequest forumRequest) {
        return Forum.builder()
                .forumName(forumRequest.getForumName())
                .description(forumRequest.getDescription())
                .createdDate(Instant.now())
                .build();
    }

    private ForumResponse mapForumToResponse(Forum forum) {
        return ForumResponse.builder()
                .id(forum.getForumId())
                .forumName(forum.getForumName())
                .description(forum.getDescription())
                .created(Utils.timeAgo(forum.getCreatedDate()))
                .numberOfPosts(postRepository.findAllByForumForumName(forum.getForumName()).size())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ForumResponse> getAll() {
        List<Forum> forums = forumRepository.findAll();
        forums.sort((f1, f2) -> f2.getCreatedDate().compareTo(f1.getCreatedDate()));     //sort by created date
        return forums.stream().map(this::mapForumToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ForumResponse getForumByName(String name) {
       // return forumRepository.findByForumName(name).get();
        return mapForumToResponse(forumRepository.findByForumName(name)
                .orElseThrow(() -> new RuntimeException("No forum found with name: " + name)));
    }

    @Transactional
    public ResponseEntity<String> updateForumDescription(String forumName, String description,String authorization) {

        checkIfUserAuthorized(authorization);

        Optional<Forum> forumOptional = forumRepository.findByForumName(forumName);

        // check if new description is empty
        if (description.length() < 1)
            return new ResponseEntity<>("Forum description cannot be empty", HttpStatus.BAD_REQUEST);

        // check if exists
        if (forumOptional.isEmpty())
            return new ResponseEntity<>("No forum found with name: " + forumName, HttpStatus.NOT_FOUND);

        Forum forum = forumOptional.get();

        forum.setDescription(description);
        forumRepository.save(forum);

        return new ResponseEntity<>("Forum description updated", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> deleteForum(String forumName,String authorization) {
      checkIfUserAuthorized(authorization);

        Optional<Forum> forumOptional = forumRepository.findByForumName(forumName);

        // check if exists
        if (forumOptional.isEmpty())
            return new ResponseEntity<>("No forum found with name: " + forumName, HttpStatus.NOT_FOUND);

        Forum forum = forumOptional.get();

        // find all post ids in this forum
        List<Long> postIds = postRepository.findAllByForumForumName(forumName)
                .stream().map(Post::getPostId).collect(Collectors.toList());

        // delete all posts in this forum
        for(Long postId : postIds) {
            postService.deletePost(postId, authorization);
        }

        // delete forum
        forumRepository.delete(forum);

        return new ResponseEntity<>("Forum " + forumName + " deleted", HttpStatus.OK);
    }
}
