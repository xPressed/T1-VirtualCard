package ru.clubcard.virtualcard.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VirtualCardQRCodeID implements Serializable {
    private Long virtualCard;
    private Long qrCode;
}
