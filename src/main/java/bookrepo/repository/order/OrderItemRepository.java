package bookrepo.repository.order;

import bookrepo.model.OrderItem;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT o FROM OrderItem o WHERE o.order.id = :orderId AND o.id = :itemId")
    OrderItem findByIdAndOrderId(@Param("orderId") Long orderId, @Param("itemId") Long itemId);

    @NotNull
    BigDecimal findByBookId(@NotNull @Positive Long bookId);
}
