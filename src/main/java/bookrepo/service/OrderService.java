package bookrepo.service;

import bookrepo.dto.order.CreateOrderDto;
import bookrepo.dto.order.OrderDto;
import bookrepo.dto.order.OrderItemDto;
import bookrepo.dto.order.UpdateOrderStatusDto;
import bookrepo.model.Book;
import bookrepo.model.Order;
import bookrepo.model.User;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<OrderDto> getAllOrders(Pageable pageable);

    OrderDto save(CreateOrderDto orderDto);

    OrderDto update(UpdateOrderStatusDto orderDto);

    OrderItemDto findByOrderId(Long id);

    OrderItemDto findSpecificOrderItem(Long orderId, Long itemId);

    void deleteById(Long id);

    Order createOrder(User user, CreateOrderDto orderDto);

    void createOrderItem(Book book, Order order, CreateOrderDto orderDto);

    BigDecimal calculateTotal(Order order);
}
