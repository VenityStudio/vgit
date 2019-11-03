package org.venity.vgit.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.venity.vgit.prototypes.UserPrototype;

@Repository
public interface UserRepository extends CrudRepository<UserPrototype, Integer> {
    boolean existsByLoginOrEmail(String login, String email);
}
