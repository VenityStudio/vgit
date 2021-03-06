package com.mwguy.vgit.dao;

import com.mwguy.vgit.Git;
import com.mwguy.vgit.VGitApplication;
import com.mwguy.vgit.configuration.GitConfiguration;
import com.mwguy.vgit.data.GitCommit;
import com.mwguy.vgit.data.GitPackType;
import com.mwguy.vgit.data.GitRepository;
import com.mwguy.vgit.data.GitTreeEntry;
import com.mwguy.vgit.exceptions.GitException;
import lombok.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Document("repositories")
public class RepositoryDao {
    public enum AccessPermission {
        PUBLIC,
        PRIVATE,
        INTERNAL
    }

    public enum PermissionType {
        GIT_PULL,
        GIT_PUSH,
        HOOK_TRIGGER;

        public static PermissionType getByPackType(GitPackType type) {
            switch (type) {
                case UPLOAD_PACK: return GIT_PULL;
                case RECEIVE_PACK: return GIT_PUSH;
            }

            return null;
        }
    }

    public enum RepositoryPathType {
        USER,
        GROUP
    }

    public enum RepositoryHookType {
        GIT_PUSH,

        PIPELINE_FAILED,
        PIPELINE_PASSED
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RepositoryPath {
        private String name;
        private String namespace;
        private RepositoryPathType type;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RepositoryHookRequestLogEntity {
        private HttpStatus status;
        private Date date;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RepositoryHook {
        private Set<RepositoryHookType> types;
        private URL url;
        private Set<RepositoryHookRequestLogEntity> requestLog;
    }

    @NonNull
    private String id;

    @NonNull
    private RepositoryPath path;

    @NonNull
    private AccessPermission accessPermission;

    @Nullable
    private String description;

    @NonNull
    private Set<String> membersIds;

    @NonNull
    private Set<RepositoryHook> hooks;

    public Boolean needAuthorization(PermissionType type) {
        if (type == PermissionType.GIT_PULL) {
            return !accessPermission.equals(AccessPermission.PUBLIC);
        } else {
            return true;
        }
    }

    public Boolean checkPermission(PermissionType type, @Nullable UserDao userDao) {
        if (type == PermissionType.GIT_PULL) {
            if (accessPermission.equals(AccessPermission.PUBLIC)) {
                return true;
            } else if (accessPermission.equals(AccessPermission.INTERNAL)) {
                return userDao != null;
            } else {
                return userDao != null && membersIds.contains(userDao.getId());
            }
        } else if (type == PermissionType.GIT_PUSH) {
            return userDao != null && membersIds.contains(userDao.getId());
        }

        return false;
    }

    public String toRepositoryPath() {
        return path.getNamespace() + "/" + path.getName();
    }

    public GitRepository toGitRepository() {
        return GitRepository.builder()
                .path(GitConfiguration.resolveGitPath(toRepositoryPath()))
                .environmentResolver(() -> new HashMap<>() {{
                    {
                        put("VGIT_REPOSITORY", toRepositoryPath());
                        put("VGIT_SECRET", GitConfiguration.gitHookSecretKey);

                        if (VGitApplication.context != null) {
                            Environment springEnvironment = VGitApplication.context.getBean(Environment.class);
                            put("VGIT_PORT", springEnvironment.getProperty("local.server.port"));
                        }
                    }
                }})
                .build();
    }

    @Data
    public static class PaginationInput {
        private Integer skip;
        private Integer limit;
    }

    @Data
    @Builder
    public static class GitTreeInput {
        private String path;
        private String object;
    }

    public List<GitCommit> getCommits(PaginationInput pagination) throws GitException {
        Git git = VGitApplication.context.getBean(Git.class);
        return git.log()
                .repository(toGitRepository())
                .skip(pagination.getSkip())
                .maxCount(pagination.getLimit())
                .build()
                .call();
    }

    public List<GitTreeEntry> getTree(GitTreeInput input) throws GitException {
        Git git = VGitApplication.context.getBean(Git.class);
        return git.lsTree()
                .repository(toGitRepository())
                .object(input.getObject())
                .path(input.getPath())
                .build()
                .call();
    }

    public String getBlob(String object) throws GitException, IOException {
        return Base64.encodeBase64String(getBlobAsInputStream(object).readAllBytes());
    }

    public InputStream getBlobAsInputStream(String object) throws GitException {
        Git git = VGitApplication.context.getBean(Git.class);
        return git.catFile()
                .repository(toGitRepository())
                .type(GitTreeEntry.GitObjectType.BLOB)
                .object(object)
                .build()
                .call();
    }
}
