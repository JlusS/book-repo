package bookrepo.mapper;

import bookrepo.config.MapperConfig;
import bookrepo.dto.user.UserRegistrationRequestDto;
import bookrepo.dto.user.UserResponseDto;
import bookrepo.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);

    User toUserModel(UserResponseDto userResponseDto);

    User toUserModel(UserRegistrationRequestDto userRegistrationRequestDto);
}
