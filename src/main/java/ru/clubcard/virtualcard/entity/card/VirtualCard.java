package ru.clubcard.virtualcard.entity.card;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class VirtualCard {
    @Id
    private Long id;

    private Long userId;

    private CardPrivilege privilege;
    private CardStatus status;

    private VirtualCardText idText;
    private VirtualCardText usernameText;
    private VirtualCardText privilegeText;
    private VirtualCardText roleText;
}
