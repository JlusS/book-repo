package bookrepo.service;

import bookrepo.dto.BookDto;
import bookrepo.dto.BookSearchParameters;
import bookrepo.dto.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    public BookDto save(CreateBookRequestDto book);

    public Page<BookDto> findAll(Pageable pageable);

    BookDto getById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParameters params);
}
