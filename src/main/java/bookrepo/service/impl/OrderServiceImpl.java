package bookrepo.service.impl;

import bookrepo.dto.order.CreateOrderDto;
import bookrepo.dto.order.OrderDto;
import bookrepo.dto.order.OrderItemDto;
import bookrepo.dto.order.UpdateOrderStatusDto;
import bookrepo.exception.EntityNotFoundException;
import bookrepo.mapper.OrderItemMapper;
import bookrepo.mapper.OrderMapper;
import bookrepo.model.Book;
import bookrepo.model.Order;
import bookrepo.model.OrderItem;
import bookrepo.model.User;
import bookrepo.repository.book.BookRepository;
import bookrepo.repository.order.OrderItemRepository;
import bookrepo.repository.order.OrderRepository;
import bookrepo.security.AuthenticationService;
import bookrepo.service.OrderService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final AuthenticationService authenticationService;
    private final BookRepository bookRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                        .map(orderMapper::toDto);
    }

    @Override
    public OrderDto save(CreateOrderDto orderDto) {
        User user = authenticationService.getAuthenticatedUser();

        Order order = orderRepository.findUserById(user.getId())
                .orElseGet(() -> createOrder(user, orderDto));

        Book book = bookRepository.findById(order.getId())
                .orElseThrow(() -> new EntityNotFoundException("Book: "
                        + orderDto.getBookId()
                        + " not found"));

        Optional<OrderItem> existingOrderItem = order.getOrderItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst();

        if (existingOrderItem.isPresent()) {
            existingOrderItem.get().setQuantity(
                    existingOrderItem.get().getQuantity()
                    + orderDto.getQuantity());
        } else {
            createOrderItem(book,order, orderDto);
        }

        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto update(UpdateOrderStatusDto orderDto) {
        User user = authenticationService.getAuthenticatedUser();

        Order order = orderRepository.findUserById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found for user id: "
                        + user.getId()));

        order.setStatus(Order.Status.valueOf(orderDto.getStatus()));

        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderItemDto findByOrderId(Long id) {
        return orderItemRepository.findById(id)
                .map(orderItemMapper::toDto).orElseThrow(() -> new EntityNotFoundException(
                        "Order item not found for id: "
                        + id));
    }

    @Override
    public OrderItemDto findSpecificOrderItem(Long orderId, Long itemId) {
        OrderItem item = orderItemRepository.findByIdAndOrderId(orderId, itemId);
        return orderItemMapper.toDto(item);
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Order createOrder(User user, CreateOrderDto orderDto) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(orderDto.getShippingAddress());
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setTotal(calculateTotal(order));
        orderRepository.save(order);
        return order;
    }

    @Override
    public void createOrderItem(Book book, Order order, CreateOrderDto orderDto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setBook(book);
        orderItem.setQuantity(orderDto.getQuantity());
        orderItem.setOrder(order);
        orderItem.setPrice(book.getPrice());
        order.getOrderItems().add(orderItem);
    }

    @Override
    public BigDecimal calculateTotal(Order order) {
        return order.getOrderItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
