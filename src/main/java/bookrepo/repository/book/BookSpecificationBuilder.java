package bookrepo.repository.book;

import bookrepo.dto.BookSearchParameters;
import bookrepo.model.Book;
import bookrepo.repository.SpecificationBuilder;
import bookrepo.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = (root, query, criteriaBuilder) -> null;

        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("author")
                    .getSpecification(searchParameters.authors()));
        }

        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("title")
                    .getSpecification(searchParameters.titles()));
        }

        if (searchParameters.isbns() != null && searchParameters.isbns().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("isbn")
                    .getSpecification(searchParameters.isbns()));
        }

        if (searchParameters.descriptions() != null && searchParameters.descriptions().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("description")
                    .getSpecification(searchParameters.descriptions()));
        }

        if (searchParameters.coverImages() != null && searchParameters.coverImages().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("coverImage")
                    .getSpecification(searchParameters.coverImages()));
        }

        if (searchParameters.prices() != null && searchParameters.prices().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("price")
                    .getSpecification(searchParameters.prices()));
        }

        return spec;
    }

}
