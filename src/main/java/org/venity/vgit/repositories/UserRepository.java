package org.venity.vgit.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.venity.vgit.prototypes.UserPrototype;

import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserPrototype, UUID> {
    boolean existsByLoginOrEmail(String login, String email);
}
