package ru.clubcard.virtualcard.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import ru.clubcard.virtualcard.TestDataLoader;
import ru.clubcard.virtualcard.entity.card.ColorBasedVirtualCard;
import ru.clubcard.virtualcard.entity.card.ImageBasedVirtualCard;
import ru.clubcard.virtualcard.entity.card.dto.*;
import ru.clubcard.virtualcard.repository.VirtualCardRepository;
import ru.clubcard.virtualcard.service.impl.VirtualCardServiceImpl;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


public class VirtualCardServiceUnitTest {
    private final VirtualCardRepository virtualCardRepository = Mockito.mock(VirtualCardRepository.class);
    private final VirtualCardMapper virtualCardMapper = new VirtualCardMapperImpl();

    private final VirtualCardServiceImpl virtualCardService = new VirtualCardServiceImpl(virtualCardRepository, virtualCardMapper);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ColorBasedVirtualCardDTO colorBasedVirtualCardDTO;
    private ImageBasedVirtualCardDTO imageBasedVirtualCardDTO;
    private ColorBasedVirtualCard colorBasedVirtualCard;
    private ImageBasedVirtualCard imageBasedVirtualCard;

    @SneakyThrows
    @BeforeEach
    public void beforeEach() {
        colorBasedVirtualCardDTO = objectMapper.readValue(
                TestDataLoader.loadData("/test/color-based-default.json"),
                ColorBasedVirtualCardDTO.class);

        imageBasedVirtualCardDTO = objectMapper.readValue(
                TestDataLoader.loadData("/test/image-based-custom.json"),
                ImageBasedVirtualCardDTO.class);

        colorBasedVirtualCard = virtualCardMapper.toColorBased(colorBasedVirtualCardDTO);
        imageBasedVirtualCard = virtualCardMapper.toImageBased(imageBasedVirtualCardDTO);
    }

    @Test
    public void getCard_Successful() {
        when(virtualCardRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(colorBasedVirtualCard));

        ResponseEntity<?> expected = ResponseEntity.ok(colorBasedVirtualCardDTO);
        ResponseEntity<?> actual = virtualCardService.getCard(1L);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void getCard_Not_Found() {
        when(virtualCardRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(colorBasedVirtualCard));

        ResponseEntity<?> expected = new ResponseEntity<>(Map.of("message", "Virtual Card not found."), HttpStatus.NOT_FOUND);
        ResponseEntity<?> actual = virtualCardService.getCard(2L);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void getCard_Null() {
        when(virtualCardRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> expected = new ResponseEntity<>(Map.of("message", "Virtual Card not found."), HttpStatus.NOT_FOUND);
        ResponseEntity<?> actual = virtualCardService.getCard(null);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @SneakyThrows
    @Test
    public void putCard_Successful() {
        when(virtualCardRepository.findById(anyLong())).thenReturn(Optional.ofNullable(colorBasedVirtualCard));
        when(virtualCardRepository.save(any())).thenReturn(imageBasedVirtualCard);

        ResponseEntity<?> expected = ResponseEntity.ok(imageBasedVirtualCardDTO);
        ResponseEntity<?> actual = virtualCardService.putCard(imageBasedVirtualCardDTO);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void putCard_Not_Found() {
        when(virtualCardRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(colorBasedVirtualCard));

        ResponseEntity<?> expected = new ResponseEntity<>(Map.of("message", "Virtual Card not found."), HttpStatus.NOT_FOUND);
        ResponseEntity<?> actual = virtualCardService.putCard(imageBasedVirtualCardDTO);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @SneakyThrows
    @Test
    public void patchCard_Successful() {
        VirtualCardDTO colorBasedCustomDTO = objectMapper.readValue(TestDataLoader.loadData("/test/color-based-custom.json"), ColorBasedVirtualCardDTO.class);

        when(virtualCardRepository.findById(anyLong())).thenReturn(Optional.ofNullable(colorBasedVirtualCard));
        when(virtualCardRepository.save(any())).thenReturn(virtualCardMapper.toVirtualCard(colorBasedCustomDTO));

        ResponseEntity<?> expected = ResponseEntity.ok(colorBasedCustomDTO);
        ResponseEntity<?> actual = virtualCardService.putCard(colorBasedCustomDTO);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void patchCard_Type_ID_Null() {
        ResponseEntity<?> expected = new ResponseEntity<>(Map.of("message", "Virtual Card TYPE and ID must be present."), HttpStatus.BAD_REQUEST);
        ResponseEntity<?> actual = virtualCardService.patchCard(new ImageBasedVirtualCardDTO());

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void patchCard_Null() {
        when(virtualCardRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> expected = new ResponseEntity<>(Map.of("message", "Virtual Card not found."), HttpStatus.NOT_FOUND);
        ResponseEntity<?> actual = virtualCardService.patchCard(imageBasedVirtualCardDTO);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void deleteCard_Successful() {
        when(virtualCardRepository.existsById(eq(1L))).thenReturn(true);

        ResponseEntity<?> expected = ResponseEntity.ok(Map.of("message", "Virtual Card deleted."));
        ResponseEntity<?> actual = virtualCardService.deleteCard(1L);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void deleteCard_Not_Found() {
        doNothing().when(virtualCardRepository).deleteById(any());
        when(virtualCardRepository.existsById(eq(1L))).thenReturn(true);

        ResponseEntity<?> expected = new ResponseEntity<>(Map.of("message", "Virtual Card not found."), HttpStatus.NOT_FOUND);
        ResponseEntity<?> actual = virtualCardService.deleteCard(2L);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void saveImage_Successful() {
        when(virtualCardRepository.findById(anyLong())).thenReturn(Optional.ofNullable(imageBasedVirtualCard));

        imageBasedVirtualCard.setImageURL("/virtual-card/image/get/1");
        when(virtualCardRepository.save(any())).thenReturn(imageBasedVirtualCard);

        imageBasedVirtualCardDTO.setImageURL("/virtual-card/image/get/1");
        ResponseEntity<?> expected = ResponseEntity.ok(imageBasedVirtualCardDTO);
        ResponseEntity<?> actual = virtualCardService.saveImage(1L, new MockMultipartFile("test", "test".getBytes()));

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void saveImage_ColorBased() {
        when(virtualCardRepository.findById(anyLong())).thenReturn(Optional.ofNullable(colorBasedVirtualCard));

        ResponseEntity<?> expected = new ResponseEntity<>(Map.of("message", "Virtual Card is not Image Based."), HttpStatus.BAD_REQUEST);
        ResponseEntity<?> actual = virtualCardService.saveImage(1L, new MockMultipartFile("test", "test".getBytes()));

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void getImage_Successful() {
        when(virtualCardRepository.findById(anyLong())).thenReturn(Optional.ofNullable(imageBasedVirtualCard));

        ResponseEntity<?> expected = ResponseEntity.ok(imageBasedVirtualCard.getImage());
        ResponseEntity<?> actual = virtualCardService.getImage(1L);

        assertNotNull(actual);
        assertEquals(expected.getBody(), actual.getBody());
    }

    @Test
    public void getImage_Bad_Request() {
        when(virtualCardRepository.findById(anyLong())).thenReturn(Optional.ofNullable(colorBasedVirtualCard));

        ResponseEntity<?> expected = ResponseEntity.badRequest().build();
        ResponseEntity<?> actual = virtualCardService.getImage(1L);

        assertNotNull(actual);
        assertEquals(expected.getStatusCode(), actual.getStatusCode());
    }
}
