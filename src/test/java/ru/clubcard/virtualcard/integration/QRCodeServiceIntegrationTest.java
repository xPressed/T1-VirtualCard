package ru.clubcard.virtualcard.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.clubcard.virtualcard.entity.VirtualCardQRCode;
import ru.clubcard.virtualcard.entity.card.ColorBasedVirtualCard;
import ru.clubcard.virtualcard.entity.qrcode.QRCode;
import ru.clubcard.virtualcard.repository.QRCodeRepository;
import ru.clubcard.virtualcard.repository.VirtualCardQRCodeRepository;
import ru.clubcard.virtualcard.repository.VirtualCardRepository;
import ru.clubcard.virtualcard.service.QRCodeService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
public class QRCodeServiceIntegrationTest {
    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test");

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private QRCodeRepository qrCodeRepository;

    @Autowired
    private VirtualCardQRCodeRepository virtualCardQRCodeRepository;

    private QRCode qrCode;

    @BeforeAll
    public static void beforeAll(@Autowired VirtualCardRepository virtualCardRepository) {
        ColorBasedVirtualCard colorBasedVirtualCard = new ColorBasedVirtualCard();
        colorBasedVirtualCard.setId(1L);
        virtualCardRepository.save(colorBasedVirtualCard);
    }

    @BeforeEach
    public void beforeEach() {
        qrCode = QRCode.builder()
                .isDisposable(false)
                .expireTime(LocalDateTime.now().plusHours(720))
                .build();
    }

    @AfterEach
    public void afterEach() {
        qrCodeRepository.deleteAll();
        virtualCardQRCodeRepository.deleteAll();
    }

    @Test
    public void generateQR_Successful() {
        ResponseEntity<?> response = qrCodeService.generate(1L, false);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void generateQR_Not_Found() {
        ResponseEntity<?> response = qrCodeService.generate(2L, false);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getQR_Successful() {
        qrCodeRepository.save(qrCode);

        ResponseEntity<?> response = qrCodeService.get(qrCode.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getQR_Not_Found() {
        qrCodeRepository.save(qrCode);

        ResponseEntity<?> response = qrCodeService.get(Long.MAX_VALUE);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getQRInfo_Successful() {
        qrCodeRepository.save(qrCode);

        qrCode.setQrCodeURL("/qr-code/get-image/" + qrCode.getId());

        ResponseEntity<?> response = qrCodeService.getInfo(qrCode.getId());
        QRCode actual = (QRCode) response.getBody();

        assertNotNull(actual);
        assertEquals(qrCode.getId(), actual.getId());
        assertEquals(qrCode.isDisposable(), actual.isDisposable());
        assertEquals(qrCode.getQrCodeURL(), actual.getQrCodeURL());

    }

    @Test
    public void getQRInfo_Not_Found() {
        qrCodeRepository.save(qrCode);

        ResponseEntity<?> response = qrCodeService.get(Long.MAX_VALUE);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getAllInfo_Empty() {
        ResponseEntity<?> response = qrCodeService.getAllInfo(1L);
        assertEquals(List.of(), response.getBody());
    }

    @Test
    public void checkQR_Successful() {
        qrCodeRepository.save(qrCode);
        virtualCardQRCodeRepository.save(new VirtualCardQRCode(ColorBasedVirtualCard.builder().id(1L).build(), qrCode));

        ResponseEntity<?> response = qrCodeService.check(qrCode.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void checkQR_Not_Found() {
        qrCodeRepository.save(qrCode);
        virtualCardQRCodeRepository.save(new VirtualCardQRCode(ColorBasedVirtualCard.builder().id(1L).build(), qrCode));

        ResponseEntity<?> response = qrCodeService.check(Long.MAX_VALUE);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void checkQR_Expired() {
        qrCode.setExpireTime(LocalDateTime.now().minusHours(3));
        qrCodeRepository.save(qrCode);
        virtualCardQRCodeRepository.save(new VirtualCardQRCode(ColorBasedVirtualCard.builder().id(1L).build(), qrCode));

        ResponseEntity<?> response = qrCodeService.check(qrCode.getId());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void deleteQR_Successful() {
        qrCodeRepository.save(qrCode);

        ResponseEntity<?> response = qrCodeService.delete(qrCode.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteQR_Not_Found() {
        qrCodeRepository.save(qrCode);

        ResponseEntity<?> response = qrCodeService.delete(Long.MAX_VALUE);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
