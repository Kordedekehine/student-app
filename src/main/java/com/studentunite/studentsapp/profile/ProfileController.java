package com.studentunite.studentsapp.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile/")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/")
    public ResponseEntity<String> createProfile(@RequestBody ProfileRequest profileRequest,@RequestParam String username, @RequestParam String authentication) {
        return profileService.createProfile(profileRequest,username, authentication);
    }

    @PutMapping("/update/{id}/{username}")
    public ResponseEntity<String> updateProfile(@ModelAttribute ProfileRequest profileRequest,
                                             @PathVariable String username ,@RequestParam String authentication,@PathVariable Long id) {
        return profileService.updateProfile(profileRequest,username,id,authentication);
    }

    @GetMapping("user/{username}")
    public ProfileResponse getProfile(@PathVariable String username) {

        ProfileResponse profileResponse = profileService.getProfileByUsername(username);
        return  profileService.getProfileByUsername(username);
    }

    @GetMapping("/{id}")
    public ProfileResponse getPostId(@PathVariable Long id) {
        return profileService.getProfileById(id);
    }

}
