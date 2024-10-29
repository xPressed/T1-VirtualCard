package ru.clubcard.virtualcard.entity.qrcode;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isDisposable;
    private LocalDateTime expireTime;

    @Transient
    private String qrCodeURL;
}
