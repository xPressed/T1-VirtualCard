package ru.clubcard.virtualcard.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.clubcard.virtualcard.entity.card.dto.VirtualCardDTO;

public interface VirtualCardService {
    ResponseEntity<?> createCard(Long userId);
    ResponseEntity<?> getCard(Long id);
    ResponseEntity<?> putCard(VirtualCardDTO data);
    ResponseEntity<?> patchCard(VirtualCardDTO data);
    ResponseEntity<?> deleteCard(Long id);

    ResponseEntity<?> saveImage(Long cardId, MultipartFile image);
    ResponseEntity<?> getImage(Long cardId);
}
