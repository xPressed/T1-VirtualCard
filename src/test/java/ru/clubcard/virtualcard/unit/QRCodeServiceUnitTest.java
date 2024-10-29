package ru.clubcard.virtualcard.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.clubcard.virtualcard.TestDataLoader;
import ru.clubcard.virtualcard.entity.VirtualCardQRCode;
import ru.clubcard.virtualcard.entity.card.ColorBasedVirtualCard;
import ru.clubcard.virtualcard.entity.card.VirtualCard;
import ru.clubcard.virtualcard.entity.card.dto.ColorBasedVirtualCardDTO;
import ru.clubcard.virtualcard.entity.card.dto.VirtualCardDTO;
import ru.clubcard.virtualcard.entity.card.dto.VirtualCardMapper;
import ru.clubcard.virtualcard.entity.card.dto.VirtualCardMapperImpl;
import ru.clubcard.virtualcard.entity.qrcode.QRCode;
import ru.clubcard.virtualcard.repository.QRCodeRepository;
import ru.clubcard.virtualcard.repository.VirtualCardQRCodeRepository;
import ru.clubcard.virtualcard.repository.VirtualCardRepository;
import ru.clubcard.virtualcard.service.impl.QRCodeServiceImpl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class QRCodeServiceUnitTest {
    private final QRCodeWriter qrCodeWriter = new QRCodeWriter();
    private final VirtualCardRepository virtualCardRepository = Mockito.mock(VirtualCardRepository.class);
    private final QRCodeRepository qrCodeRepository = Mockito.mock(QRCodeRepository.class);
    private final VirtualCardQRCodeRepository virtualCardQRCodeRepository = Mockito.mock(VirtualCardQRCodeRepository.class);
    private final VirtualCardMapper virtualCardMapper = new VirtualCardMapperImpl();

    private final QRCodeServiceImpl qrCodeService = new QRCodeServiceImpl(qrCodeWriter, virtualCardRepository, qrCodeRepository, virtualCardQRCodeRepository, virtualCardMapper);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private QRCode qrCode;

    @BeforeEach
    public void beforeEach() {
        qrCode = QRCode.builder()
                .id(1L)
                .isDisposable(false)
                .expireTime(LocalDateTime.now().plusHours(720))
                .build();
    }

    @Test
    public void generateQR_Successful() {
        when(virtualCardRepository.findById(anyLong())).thenReturn(Optional.of(new ColorBasedVirtualCard()));
        when(qrCodeRepository.save(any())).thenAnswer(invocation -> {
            QRCode saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        ResponseEntity<?> expected = ResponseEntity.ok(Map.of("message", "QR Code generated.", "id", 1L));
        ResponseEntity<?> actual = qrCodeService.generate(2L, false);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void generateQR_Not_Found() {
        when(virtualCardRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> expected = new ResponseEntity<>(Map.of("message", "Virtual Card not found."), HttpStatus.NOT_FOUND);
        ResponseEntity<?> actual = qrCodeService.generate(2L, false);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void getQR_Successful() {
        when(qrCodeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(qrCode));

        ResponseEntity<?> expected = ResponseEntity.ok().build();
        ResponseEntity<?> actual = qrCodeService.get(1L);

        assertNotNull(actual);
        assertEquals(expected.getStatusCode(), actual.getStatusCode());
    }

    @Test
    public void getQR_Not_Found() {
        when(qrCodeRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(qrCode));

        ResponseEntity<?> expected = ResponseEntity.notFound().build();
        ResponseEntity<?> actual = qrCodeService.get(2L);

        assertNotNull(actual);
        assertEquals(expected.getStatusCode(), actual.getStatusCode());
    }

    @Test
    public void getInfo_Successful() {
        when(qrCodeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(qrCode));

        qrCode.setQrCodeURL("/qr-code/get-image/1");

        ResponseEntity<?> expected = ResponseEntity.ok(qrCode);
        ResponseEntity<?> actual = qrCodeService.getInfo(1L);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void getInfo_Not_Found() {
        when(qrCodeRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> expected = new ResponseEntity<>(Map.of("message", "QR Code not found."), HttpStatus.NOT_FOUND);
        ResponseEntity<?> actual = qrCodeService.getInfo(1L);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @SneakyThrows
    @Test
    public void checkQR_Successful() {
        VirtualCardDTO virtualCardDTO = objectMapper.readValue(TestDataLoader.loadData("/test/color-based-custom.json"), ColorBasedVirtualCardDTO.class);
        VirtualCard virtualCard = virtualCardMapper.toVirtualCard(virtualCardDTO);

        VirtualCardQRCode virtualCardQRCode = new VirtualCardQRCode(virtualCard, qrCode);

        when(virtualCardQRCodeRepository.findByQrCode_Id(anyLong())).thenReturn(Optional.of(virtualCardQRCode));

        ResponseEntity<?> expected = ResponseEntity.ok(virtualCardDTO);
        ResponseEntity<?> actual = qrCodeService.check(1L);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void checkQR_Not_Found() {
        when(virtualCardQRCodeRepository.findByQrCode_Id(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> expected = new ResponseEntity<>(Map.of("message", "QR Code not found."), HttpStatus.NOT_FOUND);
        ResponseEntity<?> actual = qrCodeService.check(1L);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @SneakyThrows
    @Test
    public void checkQR_Expired() {
        VirtualCardDTO virtualCardDTO = objectMapper.readValue(TestDataLoader.loadData("/test/color-based-custom.json"), ColorBasedVirtualCardDTO.class);
        VirtualCard virtualCard = virtualCardMapper.toVirtualCard(virtualCardDTO);

        qrCode.setExpireTime(LocalDateTime.now().minusHours(3));
        VirtualCardQRCode virtualCardQRCode = new VirtualCardQRCode(virtualCard, qrCode);

        when(virtualCardQRCodeRepository.findByQrCode_Id(anyLong())).thenReturn(Optional.of(virtualCardQRCode));

        ResponseEntity<?> expected = new ResponseEntity<>(Map.of("message", "QR Code expired!"), HttpStatus.FORBIDDEN);
        ResponseEntity<?> actual = qrCodeService.check(1L);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void deleteQR_Successful() {
        when(qrCodeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(qrCode));

        ResponseEntity<?> expected = ResponseEntity.ok(Map.of("message", "QR Code deleted."));
        ResponseEntity<?> actual = qrCodeService.delete(1L);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void deleteQR_Not_Found() {
        when(qrCodeRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> expected = ResponseEntity.ok(Map.of("message", "QR Code not found."));
        ResponseEntity<?> actual = qrCodeService.delete(1L);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }
}
