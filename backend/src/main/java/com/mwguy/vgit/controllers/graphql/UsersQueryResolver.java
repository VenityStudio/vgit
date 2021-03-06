package com.mwguy.vgit.controllers.graphql;

import com.mwguy.vgit.dao.UserDao;
import com.mwguy.vgit.utils.Authorization;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

@Component
public class UsersQueryResolver implements GraphQLQueryResolver {
    public UserDao userMe() {
        return Authorization.getCurrentUser(false);
    }
}
