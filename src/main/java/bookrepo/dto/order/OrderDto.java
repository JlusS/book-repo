package bookrepo.dto.order;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private Set<OrderItemDto> orderItems;
    private String orderDate;
    @Positive
    private BigDecimal total;
    private String status;
}
