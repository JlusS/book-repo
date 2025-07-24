package bookrepo;

import java.math.BigDecimal;
import model.Book;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import service.BookService;
import service.impl.BookServiceImpl;

@SpringBootApplication
public class BookRepoApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookRepoApplication.class, args);
        Book book = new Book();
        book.setAuthor("TestBookAuthor");
        book.setCoverImage("JPEG_is_bad_idea");
        book.setDescription("idk");
        book.setIsbn("isbn");
        book.setPrice(new BigDecimal(232424));
        book.setTitle("Yeet");
        BookService bookService = new BookServiceImpl();
        bookService.save(book);
    }
}
