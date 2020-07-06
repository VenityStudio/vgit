package org.venity.vgit.prototypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@Document
@NoArgsConstructor
public class UserPrototype implements Serializable {

    @Id
    @NonNull
    private String id;

    @NonNull
    private String login;

    @NonNull
    private String fullName;

    @NonNull
    private String email;
    private String status;
    private String bio;

    private Gender gender = Gender.UNDEFINED;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdateDate;

    @NonNull
    private Set<String> projects;

    @JsonIgnore
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
