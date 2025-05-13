package com.ecom.ecommerce_backend.model.dao;

import com.ecom.ecommerce_backend.model.LocalUser;
import com.ecom.ecommerce_backend.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface VerificationTokenDAO extends ListCrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(LocalUser user);
}
