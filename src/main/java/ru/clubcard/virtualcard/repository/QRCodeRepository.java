package ru.clubcard.virtualcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.clubcard.virtualcard.entity.qrcode.QRCode;

@Repository
public interface QRCodeRepository extends JpaRepository<QRCode, Long> {
}
