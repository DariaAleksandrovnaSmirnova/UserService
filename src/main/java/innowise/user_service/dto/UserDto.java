package innowise.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Name can't be blank")
    private String name;

    @NotBlank(message = "Surname can't be blank")
    private String surname;

    @Email
    private String email;

    @DateTimeFormat
    @Past(message = "Birth date cannot be in the future")
    private LocalDate birthDate;

    private List<Long> cardIds;
}
