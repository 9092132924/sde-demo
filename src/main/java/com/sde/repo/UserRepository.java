package com.sde.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sde.model.User;

/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);
	
	User findByForgotPwdToken(String token);

	boolean existsByEmail(String email);

}
