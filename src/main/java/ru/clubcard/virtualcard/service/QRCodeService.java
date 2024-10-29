package ru.clubcard.virtualcard.service;

import org.springframework.http.ResponseEntity;

public interface QRCodeService {
    ResponseEntity<?> generate(Long virtualCardId, boolean isDisposable);
    ResponseEntity<?> get(Long qrCodeId);
    ResponseEntity<?> getInfo(Long qrCodeId);
    ResponseEntity<?> getAllInfo(Long virtualCardId);
    ResponseEntity<?> check(Long qrCodeId);
    ResponseEntity<?> delete(Long qrCodeId);
}
