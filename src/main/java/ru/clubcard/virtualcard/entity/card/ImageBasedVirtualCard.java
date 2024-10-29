package ru.clubcard.virtualcard.entity.card;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class ImageBasedVirtualCard extends VirtualCard {
    private String textColor;

    private String imageURL;

    @Lob
    private byte[] image;
}
