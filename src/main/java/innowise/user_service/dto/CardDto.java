package innowise.user_service.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {

    private Long id;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotBlank(message = "Card number cannot be blank")
    private String number;

    @NotBlank(message = "Card holder cannot be blank")
    private String holder;

    @NotNull(message = "Expiration date cannot be null")
    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;
}
