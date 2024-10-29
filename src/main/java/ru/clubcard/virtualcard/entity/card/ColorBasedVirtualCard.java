package ru.clubcard.virtualcard.entity.card;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class ColorBasedVirtualCard extends VirtualCard {
    private String textColor;
    private String backgroundColor;
}
