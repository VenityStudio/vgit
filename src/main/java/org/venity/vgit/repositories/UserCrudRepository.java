package org.venity.vgit.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.venity.vgit.prototypes.UserPrototype;

import java.util.Optional;

@Repository
public interface UserCrudRepository extends CrudRepository<UserPrototype, Integer> {
    boolean existsByLoginOrEmail(String login, String email);
    Optional<UserPrototype> findLoginAndPasswordHash(String login, byte[] passwordHash);
    Optional<UserPrototype> findByLogin(String login);
}
