package com.mwguy.vgit.utils;

import com.mwguy.vgit.dao.UserDao;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.mwguy.vgit.VGitConstants.*;

public class Authorization {

    public static UserDao getCurrentUser(boolean allowAnonymousUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BadCredentialsException(UNAUTHORIZED_MESSAGE);
        }

        Object userDetails = authentication.getPrincipal();
        if (userDetails == "anonymousUser") {
            if (!allowAnonymousUser) {
                throw new BadCredentialsException(UNAUTHORIZED_MESSAGE);
            } else {
                return null;
            }
        }

        return (UserDao) userDetails;
    }

    public static boolean isGitHookTrigger() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        return authentication.getPrincipal() == GIT_HOOK_TRIGGER;
    }

    public static String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BadCredentialsException(UNAUTHORIZED_MESSAGE);
        }

        Object credentials = authentication.getCredentials();
        if (credentials instanceof String) {
            return (String) credentials;
        }

        return null;
    }

    public static AbstractAuthenticationToken createAuthenticationToken(UserDao userDao, Object credentials) {
        return new AbstractAuthenticationToken(AuthorityUtils.NO_AUTHORITIES) {
            @Override
            public Object getCredentials() {
                return credentials;
            }

            @Override
            public Object getPrincipal() {
                return userDao;
            }
        };
    }

    public static AbstractAuthenticationToken createHookToken() {
        return new AbstractAuthenticationToken(AuthorityUtils.NO_AUTHORITIES) {
            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return GIT_HOOK_TRIGGER;
            }
        };
    }
}
