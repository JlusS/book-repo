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
import bookrepo.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final ShoppingCartService shoppingCartService;

    @Override
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                        .map(orderMapper::toDto);
    }

    @Override
    public OrderDto save(CreateOrderDto orderDto) {
        User user = authenticationService.getAuthenticatedUser();

        Order order = orderRepository.findUserById(user.getId())
                .orElseGet(() -> {
                    Order newOrder = new Order();
                    newOrder.setUser(user);
                    newOrder.setShippingAddress(orderDto.getShippingAddress());
                    newOrder.setStatus(Order.Status.PENDING);
                    newOrder.setOrderDate(LocalDateTime.now());
                    return newOrder;
                });

        Set<OrderItem> orderItems = shoppingCartService.getShoppingCart().getCartItems().stream()
                .map(cartItem -> {
                    Book book = bookRepository.findById(cartItem.getBookId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Book not found with id: " + cartItem.getBookId()));
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setBook(book);
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(book.getPrice());
                    return orderItem;
                }).collect(Collectors.toSet());

        order.getOrderItems().clear();
        order.getOrderItems().addAll(orderItems);

        BigDecimal total = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);

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
    public List<OrderItemDto> findByOrderId(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId).stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto findSpecificOrderItem(Long orderId, Long itemId) {
        User user = authenticationService.getAuthenticatedUser();
        OrderItem item = Optional.ofNullable(
                orderItemRepository.findByIdAndOrderIdAndUserId(orderId, itemId, user.getId())
        ).orElseThrow(() -> new EntityNotFoundException("Order item not found"));
        return orderItemMapper.toDto(item);
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    public Order createOrder(User user, CreateOrderDto orderDto) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(orderDto.getShippingAddress());
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setTotal(order.getOrderItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        orderRepository.save(order);
        return order;
    }
}
