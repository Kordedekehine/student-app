package com.studentunite.studentsapp.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile,Long> {

   Optional<Profile> findByUsername(String username);

   List<Profile> findAllByUsername(String username);

   void deleteAllByAppUserUsername(String username);


}
