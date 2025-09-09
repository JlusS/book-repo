package bookrepo.service;

import bookrepo.dto.order.CreateOrderDto;
import bookrepo.dto.order.OrderDto;
import bookrepo.dto.order.OrderItemDto;
import bookrepo.dto.order.UpdateOrderStatusDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<OrderDto> getAllOrders(Pageable pageable);

    OrderDto save(CreateOrderDto orderDto);

    OrderDto update(UpdateOrderStatusDto orderDto);

    List<OrderItemDto> findByOrderId(Long id);

    OrderItemDto findSpecificOrderItem(Long orderId, Long itemId);

    void deleteById(Long id);
}
