package innowise.user_service.mapper;

import innowise.user_service.dto.CardDto;
import innowise.user_service.entity.Card;
import innowise.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CardMapper {
    @Mapping(target = "userId", source = "user.id")
    CardDto toDto(Card card);

    @Mapping(target = "user", source = "userId", qualifiedByName = "userIdToUser")
    Card toEntity(CardDto cardDto);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CardDto CardDto, @MappingTarget Card card);

    @Named("userIdToUser")
    default User userIdToUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return User.builder().id(userId).build();
    }
}
