package bookrepo.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateOrderDto {
    private String shippingAddress;
    @NotNull
    @Positive
    private Long bookId;
    @Positive
    private Integer quantity;
}
