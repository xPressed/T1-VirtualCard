package ru.clubcard.virtualcard.entity.card.dto;

import org.mapstruct.*;
import ru.clubcard.virtualcard.entity.card.ColorBasedVirtualCard;
import ru.clubcard.virtualcard.entity.card.ImageBasedVirtualCard;
import ru.clubcard.virtualcard.entity.card.VirtualCard;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VirtualCardMapper {
    @SubclassMapping(source = ColorBasedVirtualCard.class, target = ColorBasedVirtualCardDTO.class)
    @SubclassMapping(source = ImageBasedVirtualCard.class, target = ImageBasedVirtualCardDTO.class)
    @Mapping(target = "type", ignore = true)
    VirtualCardDTO toDTO(VirtualCard card);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateColorBased(ColorBasedVirtualCardDTO dto, @MappingTarget ColorBasedVirtualCard card);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "imageURL", ignore = true)
    @Mapping(target = "image", ignore = true)
    void updateImageBased(ImageBasedVirtualCardDTO dto, @MappingTarget ImageBasedVirtualCard card);

    default void updateVirtualCard(VirtualCardDTO dto, VirtualCard card) {
        switch (dto.getType()) {
            case COLOR_BASED -> updateColorBased((ColorBasedVirtualCardDTO) dto, (ColorBasedVirtualCard) card);
            case IMAGE_BASED -> updateImageBased((ImageBasedVirtualCardDTO) dto, (ImageBasedVirtualCard) card);
        }
    }

    ColorBasedVirtualCard toColorBased(ColorBasedVirtualCardDTO dto);

    @Mapping(target = "imageURL", ignore = true)
    @Mapping(target = "image", ignore = true)
    ImageBasedVirtualCard toImageBased(ImageBasedVirtualCardDTO dto);

    default VirtualCard toVirtualCard(VirtualCardDTO dto) {
        return switch (dto.getType()) {
            case COLOR_BASED -> toColorBased((ColorBasedVirtualCardDTO) dto);
            case IMAGE_BASED -> toImageBased((ImageBasedVirtualCardDTO) dto);
        };
    }
}
