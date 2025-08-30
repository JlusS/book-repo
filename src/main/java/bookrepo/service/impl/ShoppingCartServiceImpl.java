package bookrepo.service.impl;

import bookrepo.dto.shoppingcart.AddToCartRequestDto;
import bookrepo.dto.shoppingcart.ShoppingCartDto;
import bookrepo.exception.EntityNotFoundException;
import bookrepo.mapper.ShoppingCartMapper;
import bookrepo.model.Book;
import bookrepo.model.CartItem;
import bookrepo.model.ShoppingCart;
import bookrepo.model.User;
import bookrepo.repository.book.BookRepository;
import bookrepo.repository.cartitem.CartItemRepository;
import bookrepo.repository.shoppingcart.ShoppingCartRepository;
import bookrepo.security.AuthenticationService;
import bookrepo.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final AuthenticationService authenticationService;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final bookrepo.mapper.CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto getShoppingCart() {
        User user = authenticationService.getAuthenticatedUser();
        ShoppingCart cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found"));
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto save(AddToCartRequestDto requestDto) {
        User user = authenticationService.getAuthenticatedUser();

        ShoppingCart cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUser(user);
                    newCart.setCartItems(new HashSet<>());
                    return shoppingCartRepository.save(newCart);
                });

        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity()
                    + requestDto.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setBook(book);
            newItem.setQuantity(requestDto.getQuantity());
            newItem.setShoppingCart(cart);
            cart.getCartItems().add(newItem);
        }

        ShoppingCart updatedCart = shoppingCartRepository.save(cart);

        return shoppingCartMapper.toDto(updatedCart);
    }

    @Override
    public ShoppingCartDto update(Long cartItemId, int quantity) {
        User user = authenticationService.getAuthenticatedUser();

        ShoppingCart cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        cartItem.setQuantity(quantity);

        ShoppingCart updatedCart = shoppingCartRepository.save(cart);

        return shoppingCartMapper.toDto(updatedCart);
    }

    @Override
    public void deleteById(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

}
