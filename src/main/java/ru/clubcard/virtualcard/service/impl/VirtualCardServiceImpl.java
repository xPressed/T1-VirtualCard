package ru.clubcard.virtualcard.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.clubcard.virtualcard.entity.card.*;
import ru.clubcard.virtualcard.entity.card.dto.VirtualCardDTO;
import ru.clubcard.virtualcard.entity.card.dto.VirtualCardMapper;
import ru.clubcard.virtualcard.repository.VirtualCardRepository;
import ru.clubcard.virtualcard.service.VirtualCardService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VirtualCardServiceImpl implements VirtualCardService {
    private final VirtualCardRepository virtualCardRepository;
    private final VirtualCardMapper virtualCardMapper;

    @Override
    public ResponseEntity<?> createCard(Long userId) {
        VirtualCard virtualCard = ColorBasedVirtualCard.builder()
                .id(virtualCardRepository.getNextSequenceValue())
                .userId(userId)
                .privilege(CardPrivilege.CARD_PRIVILEGE_STANDARD)
                .status(CardStatus.CARD_STATUS_WAITING)
                .idText(new VirtualCardText(20, false, 50, -50))
                .usernameText(new VirtualCardText(30, true, 60, 60))
                .privilegeText(new VirtualCardText(30, false, 30, 80))
                .roleText(new VirtualCardText(25, false, -40, -40))
                .textColor("black")
                .backgroundColor("white")
                .build();

        return ResponseEntity.ok(virtualCardMapper.toDTO(virtualCardRepository.save(virtualCard)));
    }

    @Override
    public ResponseEntity<?> getCard(Long id) {
        VirtualCard virtualCard = virtualCardRepository.findById(id).orElse(null);

        if (virtualCard == null) {
            return new ResponseEntity<>(Map.of("message", "Virtual Card not found."), HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(virtualCardMapper.toDTO(virtualCard));
    }

    @Override
    @Transactional
    public ResponseEntity<?> putCard(VirtualCardDTO data) {
        VirtualCard oldVirtualCard = virtualCardRepository.findById(data.getId()).orElse(null);
        if (oldVirtualCard == null) {
            return new ResponseEntity<>(Map.of("message", "Virtual Card not found."), HttpStatus.NOT_FOUND);
        }

        VirtualCard newVirtualCard = virtualCardMapper.toVirtualCard(data);

        if (oldVirtualCard instanceof ImageBasedVirtualCard oldImageBased && newVirtualCard instanceof ImageBasedVirtualCard newImageBased) {
            newImageBased.setImageURL(oldImageBased.getImageURL());
        }

        virtualCardRepository.delete(oldVirtualCard);
        return ResponseEntity.ok(virtualCardMapper.toDTO(virtualCardRepository.save(newVirtualCard)));
    }

    @Override
    public ResponseEntity<?> patchCard(VirtualCardDTO data) {
        if (data.getType() == null || data.getId() == null) {
            return new ResponseEntity<>(Map.of("message", "Virtual Card TYPE and ID must be present."), HttpStatus.BAD_REQUEST);
        }

        VirtualCard virtualCard = virtualCardRepository.findById(data.getId()).orElse(null);
        if (virtualCard == null) {
            return new ResponseEntity<>(Map.of("message", "Virtual Card not found."), HttpStatus.NOT_FOUND);
        }

        virtualCardMapper.updateVirtualCard(data, virtualCard);

        return ResponseEntity.ok(virtualCardMapper.toDTO(virtualCardRepository.save(virtualCard)));
    }

    @Override
    public ResponseEntity<?> deleteCard(Long id) {
        if (!virtualCardRepository.existsById(id)) {
            return new ResponseEntity<>(Map.of("message", "Virtual Card not found."), HttpStatus.NOT_FOUND);
        }

        virtualCardRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Virtual Card deleted."));
    }

    @SneakyThrows
    @Override
    public ResponseEntity<?> saveImage(Long cardId, MultipartFile image) {
        VirtualCard virtualCard = virtualCardRepository.findById(cardId).orElse(null);
        if (!(virtualCard instanceof ImageBasedVirtualCard imageBasedVirtualCard)) {
            return new ResponseEntity<>(Map.of("message", "Virtual Card is not Image Based."), HttpStatus.BAD_REQUEST);
        }

        imageBasedVirtualCard.setImage(image.getBytes());

        imageBasedVirtualCard.setImageURL("/virtual-card/image/get/" + imageBasedVirtualCard.getId());
        return ResponseEntity.ok(virtualCardMapper.toDTO(virtualCardRepository.save(imageBasedVirtualCard)));
    }

    @Override
    public ResponseEntity<?> getImage(Long cardId) {
        VirtualCard virtualCard = virtualCardRepository.findById(cardId).orElse(null);
        if (!(virtualCard instanceof ImageBasedVirtualCard imageBasedVirtualCard)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(imageBasedVirtualCard.getImage());
    }
}
