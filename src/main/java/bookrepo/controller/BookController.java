package bookrepo.controller;

import bookrepo.dto.BookDto;
import bookrepo.dto.BookSearchParameters;
import bookrepo.dto.CreateBookRequestDto;
import bookrepo.service.BookService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import java.lang.reflect.Constructor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.ControllerAdviceBean;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public Page<BookDto> findAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @GetMapping("/search")
    public List<BookDto> searchBooks(BookSearchParameters searchParams) {
        return bookService.search(searchParams);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteBookById(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable Long id,
                              @RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.update(id, requestDto);
    }

    @PostConstruct
    public void checkSpringVersion() {
        try {
            Constructor<?> constructor = Class.forName(
                    "org.springframework.web.method.ControllerAdviceBean")
                    .getConstructor(Object.class);
            System.out.println("✅ ControllerAdviceBean constructor found: " + constructor);
        } catch (Exception e) {
            System.err.println("❌ ControllerAdviceBean constructor missing: " + e);
        } finally {
            System.out.println("Loaded from: "
                    + ControllerAdviceBean.class.getProtectionDomain()
                    .getCodeSource().getLocation());
        }
    }
}
