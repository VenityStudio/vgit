package org.venity.vgit.prototypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Map;
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

    @Column(length = 1000)
    private String bio;

    private Gender gender = Gender.UNDEFINED;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdateDate;

    @NonNull
    @ElementCollection(targetClass = Integer.class, fetch = FetchType.EAGER)
    private Set<Integer> projects;

    @JsonIgnore
    @ElementCollection(targetClass = PublicKey.class, fetch = FetchType.EAGER)
    @Column(length = 1000)
    private Map<String, PublicKey> publicKeys;

    @NonNull
    @JsonIgnore
    private byte[] passwordHash;

    public enum Gender {
        MALE,
        FEMALE,
        UNDEFINED
    }
}
