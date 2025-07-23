package repository;

import java.util.List;
import model.Book;

public interface BookRepository {

    public Book save(Book book);

    public List<Book> findAkk();
}
