package ru.clubcard.virtualcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.clubcard.virtualcard.entity.VirtualCardQRCode;
import ru.clubcard.virtualcard.entity.VirtualCardQRCodeID;

import java.util.List;
import java.util.Optional;

@Repository
public interface VirtualCardQRCodeRepository extends JpaRepository<VirtualCardQRCode, VirtualCardQRCodeID> {
    List<VirtualCardQRCode> findAllByVirtualCard_Id(Long virtualCardId);
    Optional<VirtualCardQRCode> findByQrCode_Id(Long qrCodeId);
}
