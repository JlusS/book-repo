package bookrepo.service;

import bookrepo.dto.cartitem.CreateCartItemDto;
import bookrepo.dto.shoppingcart.ShoppingCartDto;
import bookrepo.dto.shoppingcart.UpdateCarItemQuantityDto;
import bookrepo.model.User;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart();

    ShoppingCartDto save(CreateCartItemDto requestDto);

    ShoppingCartDto update(Long id, UpdateCarItemQuantityDto quantity);

    void deleteById(Long id);

    void createShoppingCartForUser(User user);
}
