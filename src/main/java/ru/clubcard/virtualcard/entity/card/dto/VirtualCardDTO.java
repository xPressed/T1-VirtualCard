package ru.clubcard.virtualcard.entity.card.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.clubcard.virtualcard.entity.card.CardPrivilege;
import ru.clubcard.virtualcard.entity.card.CardStatus;
import ru.clubcard.virtualcard.entity.card.VirtualCardText;
import ru.clubcard.virtualcard.entity.card.VirtualCardType;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ColorBasedVirtualCardDTO.class, name = "COLOR_BASED"),
        @JsonSubTypes.Type(value = ImageBasedVirtualCardDTO.class, name = "IMAGE_BASED")
})
@Hidden
public class VirtualCardDTO {
    @NotNull(message = "Virtual Card Type must not be null!")
    private VirtualCardType type;

    @NotNull(message = "Virtual Card ID must not be null!")
    private Long id;

    @NotNull(message = "User ID must not be null!")
    private Long userId;

    @NotNull(message = "Privilege must not be null!")
    private CardPrivilege privilege;

    @NotNull(message = "Status must not be null!")
    private CardStatus status;

    @NotNull(message = "ID Text settings must not be null!")
    private VirtualCardText idText;

    @NotNull(message = "Username Text settings must not be null!")
    private VirtualCardText usernameText;

    @NotNull(message = "Privilege Text settings must not be null!")
    private VirtualCardText privilegeText;

    @NotNull(message = "Role Text settings must not be null!")
    private VirtualCardText roleText;
}
