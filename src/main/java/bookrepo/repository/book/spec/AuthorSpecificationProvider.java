package bookrepo.repository.book.spec;

import bookrepo.model.Book;
import bookrepo.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    private static final String AUTHOR_PARAM = "author";

    @Override
    public String getKey() {
        return AUTHOR_PARAM;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                root.get(AUTHOR_PARAM).in((Object[]) params);
    }
}
