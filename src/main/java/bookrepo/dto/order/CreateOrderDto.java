package bookrepo.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderDto {
    @NotBlank
    private String shippingAddress;
}
