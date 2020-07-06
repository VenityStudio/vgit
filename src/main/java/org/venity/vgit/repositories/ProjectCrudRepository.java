package org.venity.vgit.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.venity.vgit.prototypes.ProjectPrototype;

import java.util.Optional;

@Repository
public interface ProjectCrudRepository extends MongoRepository<ProjectPrototype, Integer> {
    Optional<ProjectPrototype> findByName(String name);
    boolean existsByName(String name);
}
