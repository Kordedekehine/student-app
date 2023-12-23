package com.studentunite.studentsapp.AppUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api")
public class AppUserController {
    private final AppUserService appUserService;

    @Autowired
    public AppUserController(AppUserService appUserService) {

        this.appUserService = appUserService;
    }

    // read
    @GetMapping("/userFullName/{username}")
    public ResponseEntity<AppUserResponse> getUserFullDetails(@PathVariable String username) {
        return status(HttpStatus.OK).body(appUserService.getUserFullDetailsByUsername(username));
    }

    @GetMapping("/{username}")
    public ResponseEntity<AppUserResponse> getUser(@PathVariable String username) {
        return status(HttpStatus.OK).body(appUserService.getUserByUsername(username));
    }

    // update
    @PutMapping("/updatePassword/{username}")
    public ResponseEntity<String> updatePassword(@PathVariable String username, @RequestBody String text,@RequestParam String authorization) {
        return appUserService.updateUserPassword(username, text,authorization);
    }

    // delete
    @DeleteMapping("/deleteUser/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username,@RequestParam String authorization) {
        return appUserService.deleteUser(username,authorization);
    }

}
