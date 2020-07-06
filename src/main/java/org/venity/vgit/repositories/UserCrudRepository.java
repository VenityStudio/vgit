package org.venity.vgit.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.venity.vgit.prototypes.UserPrototype;

import java.util.Optional;

@Repository
public interface UserCrudRepository extends MongoRepository<UserPrototype, String> {
    boolean existsByLoginOrEmail(String login, String email);
    Optional<UserPrototype> findByLoginAndPasswordHash(String login, byte[] passwordHash);
    Optional<UserPrototype> findByLogin(String login);
}
