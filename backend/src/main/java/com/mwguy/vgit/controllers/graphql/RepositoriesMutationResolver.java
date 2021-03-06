package com.mwguy.vgit.controllers.graphql;

import com.mwguy.vgit.dao.RepositoryDao;
import com.mwguy.vgit.exceptions.GitException;
import com.mwguy.vgit.service.RepositoriesService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;

@Component
public class RepositoriesMutationResolver implements GraphQLMutationResolver {
    private final RepositoriesService repositoriesService;

    public RepositoriesMutationResolver(RepositoriesService repositoriesService) {
        this.repositoriesService = repositoriesService;
    }

    public RepositoryDao createRepository(RepositoriesService.CreateRepositoryInput input)
            throws GitException {
        return repositoriesService.createNewRepository(input);
    }
}
