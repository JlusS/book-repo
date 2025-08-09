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
    private static final String AUTHOR = "author";
    private static final String TITLE = "title";
    private static final String ISBN = "isbn";
    private static final String DESCRIPTION = "description";
    private static final String COVER_IMAGE = "coverImage";
    private static final String PRICE = "price";
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = (root, query, criteriaBuilder) -> null;

        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(AUTHOR)
                    .getSpecification(searchParameters.authors()));
        }

        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(TITLE)
                    .getSpecification(searchParameters.titles()));
        }

        if (searchParameters.isbns() != null && searchParameters.isbns().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(ISBN)
                    .getSpecification(searchParameters.isbns()));
        }

        if (searchParameters.descriptions() != null && searchParameters.descriptions().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(DESCRIPTION)
                    .getSpecification(searchParameters.descriptions()));
        }

        if (searchParameters.coverImages() != null && searchParameters.coverImages().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(COVER_IMAGE)
                    .getSpecification(searchParameters.coverImages()));
        }

        if (searchParameters.prices() != null && searchParameters.prices().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(PRICE)
                    .getSpecification(searchParameters.prices()));
        }
        return spec;
    }
}
