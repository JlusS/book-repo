package bookrepo.service;

import bookrepo.model.Book;
import java.util.List;

public interface BookService {
    public Book save(Book book);

    public List<Book> findAll();

    Book getById(Long id);
}
