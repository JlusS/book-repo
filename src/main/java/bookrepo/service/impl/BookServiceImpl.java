package bookrepo.service.impl;

import bookrepo.dto.BookDto;
import bookrepo.dto.CreateBookRequestDto;
import bookrepo.exception.EntityNotFoundException;
import bookrepo.mapper.BookMapper;
import bookrepo.model.Book;
import bookrepo.repository.BookRepository;
import bookrepo.service.BookService;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        book.setIsbn(String.valueOf(new Random().nextInt(100_000_000)));
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto getById(Long id) {
        return bookMapper.toDto(bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id" + id)
        ));
    }
}
