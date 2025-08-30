package bookrepo.service;

import bookrepo.dto.shoppingcart.AddToCartRequestDto;
import bookrepo.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart();

    ShoppingCartDto save(AddToCartRequestDto requestDto);

    ShoppingCartDto update(Long id, int quantity);

    void deleteById(Long id);

}
