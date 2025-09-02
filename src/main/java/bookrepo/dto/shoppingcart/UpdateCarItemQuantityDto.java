package bookrepo.dto.shoppingcart;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateCarItemQuantityDto {
    @Positive
    private int quantity;
}
