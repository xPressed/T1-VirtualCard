package ru.clubcard.virtualcard.entity.card.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.clubcard.virtualcard.entity.card.VirtualCardType;

@Getter
@Setter
@ToString
public class ImageBasedVirtualCardDTO extends VirtualCardDTO {
    @NotEmpty(message = "Text Color must not be empty!")
    private String textColor;

    private String imageURL;

    public ImageBasedVirtualCardDTO() {
        this.setType(VirtualCardType.IMAGE_BASED);
    }
}
