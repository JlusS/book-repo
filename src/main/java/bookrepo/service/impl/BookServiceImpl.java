package bookrepo.service.impl;

import bookrepo.exception.EntityNotFoundException;
import bookrepo.model.Book;
import bookrepo.repository.BookRepository;
import bookrepo.service.BookService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book getById(Long id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id" + id)
        );
    }
}
