package service;

import java.util.List;
import model.Book;

public interface BookService {
    public Book save(Book book);

    public List<Book> findAll();
}
