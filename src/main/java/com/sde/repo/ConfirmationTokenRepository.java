package com.sde.repo;

import org.springframework.data.repository.CrudRepository;

import com.sde.model.ConfirmationToken;

/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, String> {
	ConfirmationToken findByConfirmationToken(String confirmationToken);
}
