package org.venity.vgit.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.venity.vgit.prototypes.RepositoryPrototype;

import java.util.Optional;

@Repository
public interface RepositoryCrudRepository extends CrudRepository<RepositoryPrototype, Integer> {
    Optional<RepositoryPrototype> findByNameAndProject(String name, String project);
}
