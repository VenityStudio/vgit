package org.venity.vgit.prototypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class UserPrototype implements Serializable {

    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NonNull
    private String login;

    @NonNull
    private String fullName;

    @NonNull
    private String email;
    private String status;

    @NonNull
    @ElementCollection(targetClass = Integer.class)
    private Set<Integer> repositoriesIds;

    @NonNull
    @JsonIgnore
    private byte[] passwordHash;
    private int avatarId = 0;
}
