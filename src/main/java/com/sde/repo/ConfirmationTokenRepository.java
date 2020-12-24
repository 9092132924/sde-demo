package com.sde.repo;

import org.springframework.data.repository.CrudRepository;

import com.sde.model.ConfirmationToken;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, String> {
	ConfirmationToken findByConfirmationToken(String confirmationToken);
}
