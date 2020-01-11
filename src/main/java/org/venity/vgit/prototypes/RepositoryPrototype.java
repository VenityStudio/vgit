package org.venity.vgit.prototypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class RepositoryPrototype {

    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NonNull
    private String project;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    private Boolean confidential;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdateDate;

    @NonNull
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    private Set<String> members;

    @JsonIgnore
    @ElementCollection(targetClass = HookPrototype.class, fetch = FetchType.EAGER)
    private Set<HookPrototype> hooks;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    private Set<String> branches;

    private String defaultBranch;
    private long commitCount = 0;
    private long branchesCount = 0;
}
