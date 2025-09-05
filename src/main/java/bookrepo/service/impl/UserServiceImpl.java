package bookrepo.service.impl;

import bookrepo.dto.user.UserRegistrationRequestDto;
import bookrepo.dto.user.UserResponseDto;
import bookrepo.exception.EntityNotFoundException;
import bookrepo.exception.RegistrationException;
import bookrepo.mapper.UserMapper;
import bookrepo.model.Role;
import bookrepo.model.RoleName;
import bookrepo.model.User;
import bookrepo.repository.role.RoleRepository;
import bookrepo.repository.user.UserRepository;
import bookrepo.service.ShoppingCartService;
import bookrepo.service.UserService;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ShoppingCartService shoppingCartService;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with this email: "
                    + requestDto.getEmail()
                    + " already exists");
        }

        User user = userMapper.toUserModel(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new EntityNotFoundException("Role '"
                        + RoleName.USER
                        + "' not found"));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);
        shoppingCartService.createShoppingCartForUser(user);
        return userMapper.toUserResponseDto(user);
    }

}
