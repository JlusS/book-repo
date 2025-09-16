package bookrepo.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bookrepo.model.Book;
import bookrepo.model.Category;
import bookrepo.repository.book.BookRepository;
import bookrepo.repository.category.CategoryRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Find all books by category ID")
    void findAllByCategories_Id_finds1BookWithId1_ShouldReturnListWith1Book() {
        Category category = new Category();
        category.setName("Programming");
        Category savedCategory = categoryRepository.save(category);

        Book book = new Book();
        book.setTitle("Book Title 1");
        book.setAuthor("Author 1");
        book.setDescription("Description 1");
        book.setIsbn("ISBN001");
        book.setPrice(BigDecimal.valueOf(29.99));
        book.setCategories(Set.of(savedCategory));

        Book savedBook = bookRepository.save(book);

        List<Book> actual = bookRepository.findAllByCategories_Id(savedCategory.getId());

        assertEquals(1, actual.size());
        assertEquals(savedBook.getId(), actual.get(0).getId());
    }
}
