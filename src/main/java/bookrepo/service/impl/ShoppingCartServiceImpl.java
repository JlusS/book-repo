package bookrepo.service.impl;

import bookrepo.dto.cartitem.CreateCartItemDto;
import bookrepo.dto.shoppingcart.ShoppingCartDto;
import bookrepo.dto.shoppingcart.UpdateCarItemQuantityDto;
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

    @Override
    public ShoppingCartDto getShoppingCart() {
        User user = authenticationService.getAuthenticatedUser();
        ShoppingCart cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user id: "
                        + user.getId()));
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto save(CreateCartItemDto requestDto) {
        User user = authenticationService.getAuthenticatedUser();

        ShoppingCart cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user id: "
                                + user.getId()));

        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book: "
                        + requestDto.getBookId()
                        + " not found"));

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

        shoppingCartRepository.save(cart);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto update(Long cartItemId, UpdateCarItemQuantityDto quantity) {
        User user = authenticationService.getAuthenticatedUser();

        ShoppingCart cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found for user id: " + user.getId()));

        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found for id: " + cartItemId));

        cartItem.setQuantity(quantity.getQuantity());

        shoppingCartRepository.save(cart);

        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public void deleteById(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public void createShoppingCartForUser(User user) {
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUser(user);
        shoppingCartRepository.save(newCart);
    }

}
