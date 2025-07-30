package bookrepo.repository;

import bookrepo.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    public Book save(Book book);

    Optional<Book> findById(Long id);

    public List<Book> findAll();

}
