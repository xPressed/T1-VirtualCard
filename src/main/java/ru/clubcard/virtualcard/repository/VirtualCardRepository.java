package ru.clubcard.virtualcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.clubcard.virtualcard.entity.card.VirtualCard;

@Repository
public interface VirtualCardRepository extends JpaRepository<VirtualCard, Long> {
    @Query(value = "SELECT NEXTVAL('virtualcard.virtual_card_id_seq')", nativeQuery = true)
    Long getNextSequenceValue();
}
