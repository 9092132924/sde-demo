package com.sde.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sde.model.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

	Profile findByEmail(String email);
	


}
