package bookrepo.repository.order;

import bookrepo.model.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi FROM OrderItem oi "
            + "WHERE oi.id =:itemId AND oi.order.id = :orderId "
            + "AND oi.order.user.id = :userId")
    OrderItem findByIdAndOrderIdAndUserId(@Param("orderId") Long orderId,
                                          @Param("itemId") Long itemId,
                                          @Param("userId") Long userId);

    List<OrderItem> findAllByOrderId(Long orderId);
}
