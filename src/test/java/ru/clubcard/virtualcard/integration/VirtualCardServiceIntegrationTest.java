package ru.clubcard.virtualcard.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
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
import ru.clubcard.virtualcard.TestDataLoader;
import ru.clubcard.virtualcard.entity.card.ColorBasedVirtualCard;
import ru.clubcard.virtualcard.entity.card.ImageBasedVirtualCard;
import ru.clubcard.virtualcard.entity.card.dto.ImageBasedVirtualCardDTO;
import ru.clubcard.virtualcard.entity.card.dto.VirtualCardMapper;
import ru.clubcard.virtualcard.repository.VirtualCardRepository;
import ru.clubcard.virtualcard.service.VirtualCardService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class VirtualCardServiceIntegrationTest {
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
    private VirtualCardService virtualCardService;

    @Autowired
    private VirtualCardRepository virtualCardRepository;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private VirtualCardMapper virtualCardMapper;

    private JsonNode jsonColorBasedDefault;
    private JsonNode jsonColorBasedCustom;
    private JsonNode jsonImageBasedCustom;

    private ColorBasedVirtualCard colorBasedDefault;
    private ColorBasedVirtualCard colorBasedCustom;
    private ImageBasedVirtualCard imageBasedCustom;

    @SneakyThrows
    @BeforeEach
    public void beforeEach() {
        JsonNode loaded = jacksonObjectMapper.readTree(TestDataLoader.loadData("/test/color-based-default.json"));
        ((ObjectNode) loaded).replace("id", LongNode.valueOf(loaded.get("id").asLong()));
        ((ObjectNode) loaded).replace("userId", LongNode.valueOf(loaded.get("userId").asLong()));
        jsonColorBasedDefault = loaded;
        colorBasedDefault = jacksonObjectMapper.treeToValue(loaded, ColorBasedVirtualCard.class);

        loaded = jacksonObjectMapper.readTree(TestDataLoader.loadData("/test/color-based-custom.json"));
        ((ObjectNode) loaded).replace("id", LongNode.valueOf(loaded.get("id").asLong()));
        ((ObjectNode) loaded).replace("userId", LongNode.valueOf(loaded.get("userId").asLong()));
        jsonColorBasedCustom = loaded;
        colorBasedCustom = jacksonObjectMapper.treeToValue(loaded, ColorBasedVirtualCard.class);

        loaded = jacksonObjectMapper.readTree(TestDataLoader.loadData("/test/image-based-custom.json"));
        ((ObjectNode) loaded).replace("id", LongNode.valueOf(loaded.get("id").asLong()));
        ((ObjectNode) loaded).replace("userId", LongNode.valueOf(loaded.get("userId").asLong()));
        jsonImageBasedCustom = loaded;
        imageBasedCustom = jacksonObjectMapper.treeToValue(loaded, ImageBasedVirtualCard.class);
    }

    @AfterEach
    public void afterEach() {
        virtualCardRepository.deleteAll();
    }

    @Test
    public void testCreateCard_Successful() {
        ResponseEntity<?> response = virtualCardService.createCard(colorBasedDefault.getId());
        JsonNode actual = jacksonObjectMapper.valueToTree(response.getBody());

        assertThat(jsonColorBasedDefault, equalTo(actual));
    }

    @Test
    public void testGetCard_Successful() {
        virtualCardRepository.save(colorBasedCustom);

        ResponseEntity<?> response = virtualCardService.getCard(colorBasedCustom.getId());
        JsonNode actual = jacksonObjectMapper.valueToTree(response.getBody());

        assertThat(jsonColorBasedCustom, equalTo(actual));
    }

    @Test
    public void testGetCard_Not_Found() {
        virtualCardRepository.save(colorBasedCustom);

        ResponseEntity<?> response = virtualCardService.getCard(Long.MAX_VALUE);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testPutCard_Successful() {
        virtualCardRepository.save(colorBasedDefault);

        ResponseEntity<?> response = virtualCardService.putCard(virtualCardMapper.toDTO(colorBasedCustom));
        JsonNode actual = jacksonObjectMapper.valueToTree(response.getBody());

        assertThat(jsonColorBasedCustom, equalTo(actual));
    }

    @Test
    public void testPutCard_Not_Found() {
        virtualCardRepository.save(colorBasedDefault);

        colorBasedCustom.setId(Long.MAX_VALUE);
        ResponseEntity<?> response = virtualCardService.putCard(virtualCardMapper.toDTO(colorBasedCustom));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testPatchCard_Successful() {
        virtualCardRepository.save(imageBasedCustom);

        ImageBasedVirtualCardDTO data = new ImageBasedVirtualCardDTO();
        data.setId(imageBasedCustom.getId());
        data.setTextColor("orange");

        ResponseEntity<?> response = virtualCardService.patchCard(data);
        JsonNode actual = jacksonObjectMapper.valueToTree(response.getBody());

        ((ObjectNode) jsonImageBasedCustom).put("textColor", "orange");

        assertThat(jsonImageBasedCustom, equalTo(actual));
    }

    @Test
    public void testPatchCard_Type_ID_Null() {
        virtualCardRepository.save(imageBasedCustom);

        ImageBasedVirtualCardDTO data = new ImageBasedVirtualCardDTO();
        data.setTextColor("orange");

        ResponseEntity<?> response = virtualCardService.patchCard(data);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testPatchCard_Not_Found() {
        virtualCardRepository.save(imageBasedCustom);

        ImageBasedVirtualCardDTO data = new ImageBasedVirtualCardDTO();
        data.setId(imageBasedCustom.getId() + 1);
        data.setTextColor("orange");

        ResponseEntity<?> response = virtualCardService.patchCard(data);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteCard_Successful() {
        virtualCardRepository.save(imageBasedCustom);

        ResponseEntity<?> response = virtualCardService.deleteCard(imageBasedCustom.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(virtualCardRepository.findById(imageBasedCustom.getId()).orElse(null));
    }

    @Test
    public void testDeleteCard_Not_Found() {
        virtualCardRepository.save(imageBasedCustom);

        ResponseEntity<?> response = virtualCardService.deleteCard(Long.MAX_VALUE);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(virtualCardRepository.findById(imageBasedCustom.getId()).orElse(null));
    }

    @Test
    public void testGetImage_Successful() {
        virtualCardRepository.save(imageBasedCustom);

        ResponseEntity<?> response = virtualCardService.getImage(imageBasedCustom.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetImage_Not_ImageBased() {
        virtualCardRepository.save(colorBasedDefault);

        ResponseEntity<?> response = virtualCardService.getImage(colorBasedDefault.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}