package com.studentunite.studentsapp.AppUser;


import com.studentunite.studentsapp.Comment.CommentRepository;
import com.studentunite.studentsapp.Forum.Forum;
import com.studentunite.studentsapp.Forum.ForumRepository;
import com.studentunite.studentsapp.Forum.ForumService;
import com.studentunite.studentsapp.Post.Post;
import com.studentunite.studentsapp.Post.PostRepository;
import com.studentunite.studentsapp.Post.PostService;
import com.studentunite.studentsapp.Utils.Utils;
import com.studentunite.studentsapp.profile.ProfileRepository;
import com.studentunite.studentsapp.profile.ProfileResponse;
import com.studentunite.studentsapp.security.securityServices.UserPrincipalService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AppUserService {
    private final PasswordEncoder passwordEncoder;

    private final PostService postService;
    private final ForumService forumService;
    private final AppUserRepository appUserRepository;
    private final ForumRepository forumRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final UserPrincipalService userPrincipalService;

    private final RestTemplate restTemplate;

    private final ProfileRepository profileRepository;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, PostService postService, ForumService forumService, ForumRepository forumRepository, PostRepository postRepository, CommentRepository commentRepository, UserPrincipalService userPrincipalService, RestTemplate restTemplate, ProfileRepository profileRepository) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.postService = postService;
        this.forumService = forumService;
        this.forumRepository = forumRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userPrincipalService = userPrincipalService;
        this.restTemplate = restTemplate;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public void createAdmin(String email) {
        String password = RandomString.make(16);

         appUserRepository.delete(appUserRepository.findByUsername("admin").orElse(null));
       //  create default admin user
        if (appUserRepository.findByUsername("admin").isEmpty()) {
            AppUser appUser = new AppUser();
            appUser.setEmail(email);
            appUser.setUsername("admin");
            appUser.setPassword(this.passwordEncoder.encode(password));
            appUser.setCreated(Instant.now());
            appUser.setAppUserRole(AppUserRole.ADMIN);
            appUser.setEnabled(true);
            appUserRepository.save(appUser);
        }
    }

    public String checkIfUserAuthorized(String authorization){
        String userEmail= userPrincipalService.getUserEmailAddressFromToken(authorization);
        if (userEmail.isEmpty()){
            throw new RuntimeException("User does not exist");
        }
        log.info(userEmail.toString());
        return userEmail;

    }

    private AppUserResponse mapAppUserToResponse(AppUser user, ProfileResponse profileResponse) {
        int numPosts = postRepository.findAllByAppUserUsername(user.getUsername()).size();
        int numComments = commentRepository.findAllByAppUserUsername(user.getUsername()).size();
        return AppUserResponse.builder()
                .userName(user.getUsername())
                .numberOfPosts(numPosts)
                .numberOfComments(numComments)
                .created(Utils.timeAgo(user.getCreated()))
                .profileResponse(profileResponse)
                .build();
    }

    private AppUserResponse mapAppUserToResponse(AppUser user) { //,ProfileResponse profileResponse
        int numPosts = postRepository.findAllByAppUserUsername(user.getUsername()).size();
        int numComments = commentRepository.findAllByAppUserUsername(user.getUsername()).size();
        return AppUserResponse.builder()
                .userName(user.getUsername())
                .numberOfPosts(numPosts)
                .numberOfComments(numComments)
                .created(Utils.timeAgo(user.getCreated()))

                .build();
    }

    @Transactional(readOnly = true)
    public AppUserResponse getUserFullDetailsByUsername(String username) {

        ProfileResponse profileResponse  = new ProfileResponse();

        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User " + username + " not found"));

        profileResponse = restTemplate.getForObject
                ("http://localhost:8080/api/profile/user/{username}",ProfileResponse.class,username);


        return mapAppUserToResponse(user,profileResponse);
    }

    @Transactional(readOnly = true)
    public AppUserResponse getUserByUsername(String username) {

        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User " + username + " not found"));

        return mapAppUserToResponse(user);
    }

    @Transactional
    public ResponseEntity<String> updateUserPassword(String username, String password,String authorization) {

        checkIfUserAuthorized(authorization);

        Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

        // check if exists
        if (userOptional.isEmpty())
            return new ResponseEntity<>("User " + username + " not found", HttpStatus.NOT_FOUND);

        AppUser user = userOptional.get();

        user.setPassword(passwordEncoder.encode(password));
        appUserRepository.save(user);
        return new ResponseEntity<>("Password updated!", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> deleteUser(String username,String authorization) {

        checkIfUserAuthorized(authorization);

        Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

        // check if exists
        if (userOptional.isEmpty())
            return new ResponseEntity<>("User " + username + " not found", HttpStatus.NOT_FOUND);

        AppUser user = userOptional.get();

        // delete all comments by this user
        commentRepository.deleteAllByAppUserUsername(username);

        //delete the user profile
        profileRepository.deleteAllByAppUserUsername(username);

        // delete all posts by this user
        List<Post> postsByUser = postRepository.findAllByAppUserUsername(username);
        for(var post: postsByUser)
            postService.deletePost(post.getPostId(), authorization);

        // delete all forums by this user
        List<Forum> forumsByUser = forumRepository.findAllByAppUserUsername(username);
        for(var forum: forumsByUser)
            forumService.deleteForum(forum.getForumName(),authorization);

        // delete user
        appUserRepository.delete(user);

        return new ResponseEntity<>("User deleted", HttpStatus.OK);
    }
}
