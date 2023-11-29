package com.studentunite.studentsapp.profile;

import com.studentunite.studentsapp.AppUser.AppUser;
import com.studentunite.studentsapp.AppUser.AppUserRepository;
import com.studentunite.studentsapp.security.securityServices.UserPrincipalService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class ProfileService {

    private final ProfileRepository profileRepository;

    private final AppUserRepository appUserRepository;

    private final UserPrincipalService userPrincipalService;

    private final ModelMapper modelMapper;

    public ProfileService(ProfileRepository profileRepository, AppUserRepository appUserRepository, UserPrincipalService userPrincipalService, ModelMapper modelMapper) {
        this.profileRepository = profileRepository;
        this.appUserRepository = appUserRepository;
        this.userPrincipalService = userPrincipalService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ResponseEntity<String> createProfile(ProfileRequest profileRequest,String username,String authorization) {

        checkIfUserAuthorized(authorization);

        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User with username " + username.toString() + " not found"));

        if (profileRequest.getBio().length() > 170)
            return new ResponseEntity<>("Bio title cannot be longer than 150 characters", HttpStatus.BAD_REQUEST);

        if (profileRequest.getProfilePicture() != null && profileRequest.getProfilePicture().getBytes(StandardCharsets.UTF_8).length > 1000000)
            return new ResponseEntity<>("Profile Picture cannot be larger than 1MB", HttpStatus.BAD_REQUEST);


        if (profileRequest.getBackgroundPicture() != null && profileRequest.getBackgroundPicture().getBytes(StandardCharsets.UTF_8).length > 1000000)
            return new ResponseEntity<>("Background Image cannot be larger than 1MB", HttpStatus.BAD_REQUEST);

        Profile profile = new Profile();
        profile.setProfilePicture(profileRequest.getBackgroundPicture().getBytes(StandardCharsets.UTF_8));
        profile.setBackgroundPicture(profileRequest.getBackgroundPicture().getBytes(StandardCharsets.UTF_8));
        profile.setUrl(profileRequest.getUrl());
        profile.setCountry(profileRequest.getCountry());
        profile.setBio(profileRequest.getBio());
        profile.setDepartment(profileRequest.getDepartment());
        profile.setUsername(appUser.getUsername());

        profileRepository.save(profile);

        modelMapper.map(profile,profileRequest);

        ProfileResponse profileResponse = new ProfileResponse();
        modelMapper.map(profileRequest,profileResponse);

        log.info(profileResponse.getUsername());
        return new ResponseEntity<>("Profile Saved!" ,HttpStatus.ACCEPTED);
    }

    public String checkIfUserAuthorized(String authorization){

        String userEmail= userPrincipalService.getUserEmailAddressFromToken(authorization);

        if (userEmail.isEmpty()){

            throw new RuntimeException("User does not exist");
        }

        return userEmail;
    }

    @Transactional
    public ResponseEntity<String> updateProfile(ProfileRequest profileRequest,String username,Long id,String authorization) {

        checkIfUserAuthorized(authorization);

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile with id " + id.toString() + " not found"));
        // check if exists

        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User with username " + username.toString() + " not found"));

        profile.setProfilePicture(profileRequest.getProfilePicture().getBytes(StandardCharsets.UTF_8));
        profile.setBackgroundPicture(profileRequest.getBackgroundPicture().getBytes(StandardCharsets.UTF_8));
        profile.setBio(profileRequest.getBio());
        profile.setDepartment(profileRequest.getDepartment());
        profile.setCountry(profileRequest.getCountry());
        profile.setUrl(profileRequest.getUrl());
        profile.setUsername(appUser.getUsername());
        profile.setAppUser(appUser);

        profileRepository.save(profile);

        return new ResponseEntity<>("Post updated" , HttpStatus.OK);
    }

    private ProfileResponse mapProfileToResponse(Profile profile) {
        return ProfileResponse.builder()
                .profilePicture(new String(profile.getProfilePicture()))
                .backgroundPicture(new String(profile.getBackgroundPicture()))
                .bio(profile.getBio())
                .country(profile.getCountry())
                .department(profile.getDepartment())
                .url(profile.getUrl())
                .country(profile.getCountry())
                .build();
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileById(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile with id " + id.toString() + " not found"));
        return mapProfileToResponse(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUsername(String username) {

        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User with username " + username.toString() + " not found"));
       // return mapProfileToResponse(profile);

        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile with username " + username.toString() + " not found"));

        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setProfilePicture( new String(profile.getProfilePicture()));
        profileResponse.setBackgroundPicture( new String(profile.getBackgroundPicture()));
        profileResponse.setBio(profile.getBio());
        profileResponse.setCountry(profile.getCountry());
        profileResponse.setDepartment(profile.getDepartment());
        profileResponse.setUrl(profile.getUrl());
        profileResponse.setUsername(appUser.getUsername());

       return profileResponse;
    }
}