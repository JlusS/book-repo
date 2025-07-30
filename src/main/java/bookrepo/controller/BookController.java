package bookrepo.controller;

import bookrepo.dto.BookDto;
import bookrepo.dto.CreateBookRequestDto;
import bookrepo.mapper.BookMapper;
import bookrepo.model.Book;
import bookrepo.service.BookService;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/books")
public class BookController {
    private final BookService bookService;
    private final BookMapper bookMapper;

    @GetMapping
    public List<BookDto> findAll() {
        return bookService.findAll().stream().map(bookMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookMapper.toDto(bookService.getById(id));
    }

    @PostMapping
    public BookDto createBook(@RequestBody CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        book.setIsbn(String.valueOf(new Random().nextInt(1000000000)));
        return bookMapper.toDto(bookService.save(book));
    }
}
