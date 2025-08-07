package bookrepo.mapper;

import bookrepo.config.MapperConfig;
import bookrepo.dto.BookDto;
import bookrepo.dto.CreateBookRequestDto;
import bookrepo.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    void updateModelFromDto(CreateBookRequestDto requestDto, @MappingTarget Book book);
}
