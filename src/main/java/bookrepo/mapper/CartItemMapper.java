package bookrepo.mapper;

import bookrepo.dto.cartitem.CartItemDto;
import bookrepo.dto.cartitem.CreateCartItemDto;
import bookrepo.model.CartItem;
import bookrepo.repository.book.BookRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public interface CartItemMapper {

    @Mapping(source = "bookId", target = "book", qualifiedByName = "bookFromId")
    CartItem toModel(CreateCartItemDto dto, @Context BookRepository bookRepository);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem cartItem);
}
