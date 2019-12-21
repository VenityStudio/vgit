package org.venity.vgit.repositories;

import org.springframework.data.repository.CrudRepository;
import org.venity.vgit.prototypes.ProjectPrototype;

import java.util.Optional;

public interface ProjectCrudRepository extends CrudRepository<ProjectPrototype, Integer> {
    Optional<ProjectPrototype> findByName(String name);
    boolean existsByName(String name);
}
