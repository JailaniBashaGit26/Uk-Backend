package com.uk.uk.repository;

import com.uk.uk.entity.TempUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TempUserRepo extends CrudRepository<TempUser, Long> {
    //Optional<TempUser> findByUsername(String username);
    List<TempUser> findByUserEmail(String name);
}
