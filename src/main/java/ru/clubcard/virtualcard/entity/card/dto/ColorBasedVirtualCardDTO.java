package ru.clubcard.virtualcard.entity.card.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import ru.clubcard.virtualcard.entity.card.VirtualCardType;

@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ColorBasedVirtualCardDTO extends VirtualCardDTO {
    @NotEmpty(message = "Text Color must not be empty!")
    private String textColor;

    @NotEmpty(message = "Background Color must not be empty!")
    private String backgroundColor;

    public ColorBasedVirtualCardDTO() {
        this.setType(VirtualCardType.COLOR_BASED);
    }
}
