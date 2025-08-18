package bookrepo.dto.user;

import bookrepo.validation.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@PasswordMatches
public class UserRegistrationRequestDto {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 16;

    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Length(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    private String password;
    @NotBlank
    @Length(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}
