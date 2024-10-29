package ru.clubcard.virtualcard.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.clubcard.virtualcard.entity.VirtualCardQRCode;
import ru.clubcard.virtualcard.entity.card.VirtualCard;
import ru.clubcard.virtualcard.entity.card.dto.VirtualCardMapper;
import ru.clubcard.virtualcard.entity.qrcode.QRCode;
import ru.clubcard.virtualcard.repository.QRCodeRepository;
import ru.clubcard.virtualcard.repository.VirtualCardQRCodeRepository;
import ru.clubcard.virtualcard.repository.VirtualCardRepository;
import ru.clubcard.virtualcard.service.QRCodeService;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class QRCodeServiceImpl implements QRCodeService {
    private final QRCodeWriter qrCodeWriter;
    private final VirtualCardRepository virtualCardRepository;
    private final QRCodeRepository qrCodeRepository;
    private final VirtualCardQRCodeRepository virtualCardQRCodeRepository;
    private final VirtualCardMapper virtualCardMapper;

    @Value("${clubcard.qr-code.default-expire-hours}")
    private long expireHours;

    @Value("${clubcard.qr-code.width}")
    private int width;

    @Value("${clubcard.qr-code.height}")
    private int height;

    @Override
    @Transactional
    public ResponseEntity<?> generate(Long virtualCardId, boolean isDisposable) {
        VirtualCard virtualCard = virtualCardRepository.findById(virtualCardId).orElse(null);
        if (virtualCard == null) {
            return new ResponseEntity<>(Map.of("message", "Virtual Card not found."), HttpStatus.NOT_FOUND);
        }

        QRCode qrCode = QRCode.builder()
                .isDisposable(isDisposable)
                .expireTime(LocalDateTime.now().plusHours(expireHours))
                .build();

        VirtualCardQRCode virtualCardQRCode = new VirtualCardQRCode(virtualCard, qrCode);

        qrCodeRepository.save(qrCode);
        virtualCardQRCodeRepository.save(virtualCardQRCode);
        return ResponseEntity.ok(Map.of("message", "QR Code generated.", "id", qrCode.getId()));
    }

    @Override
    public ResponseEntity<?> get(Long qrCodeId) {
        QRCode qrCode = qrCodeRepository.findById(qrCodeId).orElse(null);
        if (qrCode == null) {
            return ResponseEntity.notFound().build();
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCode.getId().toString(), BarcodeFormat.QR_CODE, width, height);
            ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "JPEG", baos);

            return ResponseEntity.ok(baos.toByteArray());
        } catch (WriterException | IOException e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<?> getInfo(Long qrCodeId) {
        QRCode qrCode = qrCodeRepository.findById(qrCodeId).orElse(null);
        if (qrCode == null) {
            return new ResponseEntity<>(Map.of("message", "QR Code not found."), HttpStatus.NOT_FOUND);
        }

        qrCode.setQrCodeURL("/qr-code/get-image/" + qrCode.getId());

        return ResponseEntity.ok(qrCode);
    }

    @Override
    @Transactional
    public ResponseEntity<?> getAllInfo(Long virtualCardId) {
        return ResponseEntity.ok(virtualCardQRCodeRepository.findAllByVirtualCard_Id(virtualCardId).stream()
                .map(VirtualCardQRCode::getQrCode)
                .peek(qrCode -> qrCode.setQrCodeURL("/qr-code/get-image/" + qrCode.getId()))
                .toList());
    }

    @Override
    @Transactional
    public ResponseEntity<?> check(Long qrCodeId) {
        VirtualCardQRCode virtualCardQRCode = virtualCardQRCodeRepository.findByQrCode_Id(qrCodeId).orElse(null);

        if (virtualCardQRCode == null) {
            return new ResponseEntity<>(Map.of("message", "QR Code not found."), HttpStatus.NOT_FOUND);
        }

        QRCode qrCode = virtualCardQRCode.getQrCode();

        if (qrCode.getExpireTime().isBefore(LocalDateTime.now())) {
            virtualCardQRCodeRepository.delete(virtualCardQRCode);
            qrCodeRepository.delete(qrCode);
            return new ResponseEntity<>(Map.of("message", "QR Code expired!"), HttpStatus.FORBIDDEN);
        }

        if (qrCode.isDisposable()) {
            virtualCardQRCodeRepository.delete(virtualCardQRCode);
            qrCodeRepository.delete(qrCode);
        }

        return ResponseEntity.ok(virtualCardMapper.toDTO(virtualCardQRCode.getVirtualCard()));
    }

    @Override
    public ResponseEntity<?> delete(Long qrCodeId) {
        QRCode qrCode = qrCodeRepository.findById(qrCodeId).orElse(null);
        if (qrCode == null) {
            return new ResponseEntity<>(Map.of("message", "QR Code not found."), HttpStatus.NOT_FOUND);
        }

        qrCodeRepository.delete(qrCode);
        return ResponseEntity.ok(Map.of("message", "QR Code deleted."));
    }
}
