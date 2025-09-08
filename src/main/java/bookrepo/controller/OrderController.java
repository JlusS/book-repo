package bookrepo.controller;

import bookrepo.dto.order.CreateOrderDto;
import bookrepo.dto.order.OrderDto;
import bookrepo.dto.order.OrderItemDto;
import bookrepo.dto.order.UpdateOrderStatusDto;
import bookrepo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "Endpoints for managing orders and order items")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all orders", description =
            "Returns a paginated list of all orders. Accessible only by ADMIN.")
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Create a new order", description =
            "Creates a new order for the authenticated USER.")
    public OrderDto createOrder(@RequestBody @Valid CreateOrderDto orderDto) {
        return orderService.save(orderDto);
    }

    @PatchMapping
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Update order status", description =
            "Updates the status of an existing order. Accessible by USER.")
    public OrderDto updateOrderStatus(
            @RequestBody @Valid UpdateOrderStatusDto orderDto) {
        return orderService.update(orderDto);
    }

    @GetMapping("/{id}/items")
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Get order items by order ID", description =
            "Returns all items for a specific order. Accessible by USER.")
    public OrderItemDto getOrderItems(@PathVariable Long id) {
        return orderService.findByOrderId(id);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Get specific order item", description =
            "Returns a specific item from an order by order ID and item ID.")
    public OrderItemDto getSpecificOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {
        return orderService.findSpecificOrderItem(orderId, itemId);
    }
}
