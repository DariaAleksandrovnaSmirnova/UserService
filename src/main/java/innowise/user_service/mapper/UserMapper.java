package innowise.user_service.mapper;

import innowise.user_service.dto.UserDto;
import innowise.user_service.entity.Card;
import innowise.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "cardIds", source = "cards", qualifiedByName = "mapCards")
    UserDto toDto(User user);

    @Mapping(target = "cards", ignore = true)
    User toEntity(UserDto userDto);

    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UserDto userDto, @MappingTarget User user);

    @Named("mapCards")
    default List<Long> mapCardsToCardsId(List<Card> cards) {
        if (cards == null) {
            return Collections.emptyList();
        }
        return cards.stream()
                .map(Card::getId)
                .toList();
    }
}
