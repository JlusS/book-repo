package bookrepo;

import bookrepo.model.Book;
import bookrepo.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookRepoApplication {

    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookRepoApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(BookService bookService) {
        return args -> {
            Book book = new Book();
            book.setAuthor("TestBookAuthor");
            book.setCoverImage("JPEG_is_bad_idea");
            book.setDescription("idk");
            book.setIsbn("isbn");
            book.setPrice(new BigDecimal("232424"));
            book.setTitle("Yeet");

            Book book1 = new Book();
            book1.setAuthor("TestBookAuthor1");
            book1.setCoverImage("JPEG_is_bad_idea1");
            book1.setDescription("idk1");
            book1.setIsbn("isbn1");
            book1.setPrice(new BigDecimal("2324248"));
            book1.setTitle("Yeet1");

            bookService.save(book);
            bookService.save(book1);

            System.out.println("Books saved successfully!");
        };
    }
}
