package bookrepo.repository.book;

import bookrepo.model.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    List<Book> findAllByCategories_Id(Long categoryId);

    @Query("SELECT b FROM Book b JOIN FETCH b.categories WHERE b.id = :id")
    Optional<Book> findByIdWithCategories(@Param("id") Long id);

}
