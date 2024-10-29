package ru.clubcard.virtualcard.entity.card;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VirtualCardText implements Serializable {
    private int pxSize;
    private boolean isBold;

    private int positionX;
    private int positionY;
}
