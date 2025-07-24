package bookrepo.repository.impl;

import bookrepo.model.Book;
import bookrepo.repository.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BookRepositoryImpl implements BookRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Book save(Book book) {
        entityManager.persist(book);
        return book;
    }

    @Override
    public List<Book> findAll() {
        CriteriaQuery<Book> criteriaQuery = entityManager
                .getCriteriaBuilder().createQuery(Book.class);
        criteriaQuery.from(Book.class);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
