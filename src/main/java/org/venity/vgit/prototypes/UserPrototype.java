package org.venity.vgit.prototypes;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Data
@RedisHash("users")
@NoArgsConstructor
public class UserPrototype implements Serializable {

    @Id
    @NonNull
    private UUID uuid;

    @NonNull
    private String login;

    @NonNull
    private String fullName;

    @NonNull
    private String email;
    private String status;

    @NonNull
    private Set<UUID> repositoriesIds;

    @NonNull
    private byte[] passwordHash;
    private int avatarId = 0;
}
