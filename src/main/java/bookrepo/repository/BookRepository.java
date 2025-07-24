package bookrepo.repository;

import bookrepo.model.Book;
import java.util.List;

public interface BookRepository {

    public Book save(Book book);

    public List<Book> findAll();
}
