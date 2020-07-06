package org.venity.vgit.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.venity.vgit.prototypes.RepositoryPrototype;

import java.util.Optional;

@Repository
public interface RepositoryCrudRepository extends MongoRepository<RepositoryPrototype, String> {
    Optional<RepositoryPrototype> findByNameAndProject(String name, String project);
}
