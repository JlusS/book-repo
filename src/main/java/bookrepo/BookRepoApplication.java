package bookrepo;

import config.AppConfig;
import java.math.BigDecimal;
import model.Book;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import service.BookService;

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

        Book book1 = new Book();
        book1.setAuthor("TestBookAuthor1");
        book1.setCoverImage("JPEG_is_bad_idea1");
        book1.setDescription("idk1");
        book1.setIsbn("isbn1");
        book1.setPrice(new BigDecimal(2324248));
        book1.setTitle("Yeet1");

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        BookService bookService = context.getBean(BookService.class);

        bookService.save(book);
        bookService.save(book1);
    }
}
