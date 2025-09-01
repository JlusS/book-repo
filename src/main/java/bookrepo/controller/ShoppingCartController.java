package bookrepo.controller;

import bookrepo.dto.cartitem.CreateCartItemDto;
import bookrepo.dto.shoppingcart.ShoppingCartDto;
import bookrepo.dto.shoppingcart.UpdateCarItemQuantityDto;
import bookrepo.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping Cart", description = "Endpoints for managing user's shopping cart")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @Operation(summary = "Get current user's shopping cart")
    public ShoppingCartDto getShoppingCart() {
        return shoppingCartService.getShoppingCart();
    }

    @Operation(summary = "Add a book to the shopping cart")
    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ShoppingCartDto addToCart(@RequestBody @Valid CreateCartItemDto requestDto) {
        return shoppingCartService.save(requestDto);
    }

    @Operation(summary = "Update quantity of a cart item")
    @PutMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasAuthority('USER')")
    public ShoppingCartDto updateCartItem(@PathVariable Long cartItemId,
                                          @RequestParam UpdateCarItemQuantityDto quantity) {
        return shoppingCartService.update(cartItemId, quantity);
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Remove a book from the shopping cart")
    public void deleteCartItem(@PathVariable Long cartItemId) {
        shoppingCartService.deleteById(cartItemId);
    }
}
