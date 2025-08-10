package bookrepo.repository.book.spec;

import bookrepo.model.Book;
import bookrepo.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DescriptionSpecificationProvider implements SpecificationProvider<Book> {
    private static final String DESCRIPTION = "description";

    @Override
    public String getKey() {
        return DESCRIPTION;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                root.get(DESCRIPTION).in(Arrays.stream(params).toArray());
    }
}
