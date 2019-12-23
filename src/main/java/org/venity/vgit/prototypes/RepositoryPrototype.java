package org.venity.vgit.prototypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
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

    @NonNull
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    private Set<String> members;

    @JsonIgnore
    @ElementCollection(targetClass = HookPrototype.class, fetch = FetchType.EAGER)
    private Set<HookPrototype> hooks;
}
