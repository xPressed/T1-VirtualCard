package ru.clubcard.virtualcard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.clubcard.virtualcard.entity.card.VirtualCard;
import ru.clubcard.virtualcard.entity.qrcode.QRCode;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(VirtualCardQRCodeID.class)
public class VirtualCardQRCode {
    @Id
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "virtual_card_id")
    private VirtualCard virtualCard;

    @Id
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "qr_code_id")
    private QRCode qrCode;
}
