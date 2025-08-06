package bookrepo.service;

import bookrepo.dto.BookDto;
import bookrepo.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    public BookDto save(CreateBookRequestDto book);

    public List<BookDto> findAll();

    BookDto getById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    void deleteById(Long id);
}
