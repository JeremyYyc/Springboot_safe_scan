package org.safescan.DTO;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class AttributesDTO {
    private boolean isPregnant;
    private boolean isChildren;
    private boolean isElderly;
    private boolean isDisabled;
    private boolean isAllergic;
    private boolean isPets;

    public AttributesDTO() {
        this.isPregnant = false;
        this.isChildren = false;
        this.isElderly = false;
        this.isDisabled = false;
        this.isAllergic = false;
        this.isPets = false;
    }
}
