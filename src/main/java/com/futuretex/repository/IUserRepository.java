package com.futuretex.repository;

import com.futuretex.domain.User;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface IUserRepository extends CrudRepository<User, Long> {

}
