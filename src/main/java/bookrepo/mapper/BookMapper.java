package bookrepo.mapper;

import bookrepo.config.MapperConfig;
import bookrepo.dto.book.BookDto;
import bookrepo.dto.book.BookDtoWithoutCategoryIds;
import bookrepo.dto.book.CreateBookRequestDto;
import bookrepo.model.Book;
import bookrepo.model.Category;
import bookrepo.repository.book.BookRepository;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    void updateModelFromDto(CreateBookRequestDto requestDto, @MappingTarget Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @Named("bookFromId")
    default Book bookFromId(Long id, @Context BookRepository bookRepository) {
        return bookRepository.getReferenceById(id);
    }

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        Set<Long> ids = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        bookDto.setCategoryIds(ids);
    }
}
