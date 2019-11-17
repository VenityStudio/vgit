package org.venity.vgit.prototypes;

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
    private String namespace;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    private Integer owner;

    @NonNull
    private Boolean confidential;

    @NonNull
    @ElementCollection(targetClass = Integer.class, fetch = FetchType.EAGER)
    private Set<Integer> maintainers;

    @NonNull
    @ElementCollection(targetClass = Integer.class, fetch = FetchType.EAGER)
    private Set<Integer> members;
}
