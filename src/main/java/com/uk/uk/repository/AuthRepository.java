package com.uk.uk.repository;

import com.uk.uk.entity.AuthEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthRepository extends CrudRepository<AuthEntity, Long> {
    AuthEntity findByUserEmail(String userEmail);
}
