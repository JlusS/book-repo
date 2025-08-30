package bookrepo.dto.shoppingcart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddToCartRequestDto {
    private Long bookId;
    private int quantity;
}
