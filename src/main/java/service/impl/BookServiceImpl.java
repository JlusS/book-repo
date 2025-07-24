package service.impl;

import java.util.List;
import model.Book;
import org.springframework.stereotype.Service;
import repository.impl.BookRepositoryImpl;
import service.BookService;

@Service
public class BookServiceImpl implements BookService {
    private BookRepositoryImpl bookRepository;

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}
