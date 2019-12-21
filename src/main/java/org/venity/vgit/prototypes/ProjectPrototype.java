package org.venity.vgit.prototypes;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class ProjectPrototype {

    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NonNull
    private String name;

    private String description;

    @NonNull
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    private Set<String> members;

    @ElementCollection(targetClass = Integer.class, fetch = FetchType.EAGER)
    private Set<Integer> repositories;
}
