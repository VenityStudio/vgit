package org.venity.vgit.prototypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Document
@NoArgsConstructor
public class RepositoryPrototype {

    @Id
    @NonNull
    private String id;

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
    private Set<String> members;

    @JsonIgnore
    private Set<HookPrototype> hooks;
    private Set<String> branches;

    private String defaultBranch;
    private long commitCount = 0;
    private long branchesCount = 0;
}
