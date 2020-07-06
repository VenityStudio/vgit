package org.venity.vgit.prototypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HookPrototype {
    private HookType type;

    @Size(max = 100)
    private String url;
    private String name;

    public enum HookType {
        PUSH
    }
}
