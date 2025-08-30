package bookrepo.mapper;

import bookrepo.config.MapperConfig;
import bookrepo.dto.shoppingcart.ShoppingCartDto;
import bookrepo.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    ShoppingCartDto toDto(ShoppingCart cart);
}
