package bookrepo.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import bookrepo.dto.book.BookSearchParameters;
import bookrepo.model.Book;
import bookrepo.repository.book.BookSpecificationBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookSpecificationBuilderTest {

    @Mock
    private SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Mock
    private SpecificationProvider<Book> specificationProvider;

    @InjectMocks
    private BookSpecificationBuilder bookSpecificationBuilder;

    @Test
    void build_WithAuthorParameters_ShouldAddAuthorSpecification() {
        // Given
        BookSearchParameters searchParameters = new BookSearchParameters(
                new String[]{"Author1", "Author2"}, null, null, null, null, null
        );

        when(bookSpecificationProviderManager.getSpecificationProvider("author"))
                .thenReturn(specificationProvider);
        when(specificationProvider.getSpecification(any()))
                .thenReturn((root, query, cb) -> cb.conjunction());

        // When
        Specification<Book> result = bookSpecificationBuilder.build(searchParameters);

        // Then
        assertNotNull(result);
        verify(bookSpecificationProviderManager).getSpecificationProvider("author");
        verify(specificationProvider).getSpecification(new String[]{"Author1", "Author2"});
    }

    @Test
    void build_WithTitleParameters_ShouldAddTitleSpecification() {
        // Given
        BookSearchParameters searchParameters = new BookSearchParameters(
                null, new String[]{"Title1", "Title2"}, null, null, null, null
        );

        when(bookSpecificationProviderManager.getSpecificationProvider("title"))
                .thenReturn(specificationProvider);
        when(specificationProvider.getSpecification(any()))
                .thenReturn((root, query, cb) -> cb.conjunction());

        // When
        Specification<Book> result = bookSpecificationBuilder.build(searchParameters);

        // Then
        assertNotNull(result);
        verify(bookSpecificationProviderManager).getSpecificationProvider("title");
        verify(specificationProvider).getSpecification(new String[]{"Title1", "Title2"});
    }

    @Test
    void build_WithMultipleParameters_ShouldCombineSpecifications() {
        // Given
        BookSearchParameters searchParameters = new BookSearchParameters(
                new String[]{"Author1"},
                new String[]{"Title1"},
                new String[]{"ISBN123"},
                null, null, null
        );

        when(bookSpecificationProviderManager.getSpecificationProvider("author"))
                .thenReturn(specificationProvider);
        when(bookSpecificationProviderManager.getSpecificationProvider("title"))
                .thenReturn(specificationProvider);
        when(bookSpecificationProviderManager.getSpecificationProvider("isbn"))
                .thenReturn(specificationProvider);
        when(specificationProvider.getSpecification(any()))
                .thenReturn((root, query, cb) -> cb.conjunction());

        // When
        Specification<Book> result = bookSpecificationBuilder.build(searchParameters);

        // Then
        assertNotNull(result);
        verify(bookSpecificationProviderManager, times(3)).getSpecificationProvider(anyString());
    }

    @Test
    void build_WithNullParameters_ShouldReturnBaseSpecification() {
        // Given
        BookSearchParameters searchParameters = new BookSearchParameters(
                null, null, null, null, null, null
        );

        // When
        Specification<Book> result = bookSpecificationBuilder.build(searchParameters);

        // Then
        assertNotNull(result);
        verifyNoInteractions(bookSpecificationProviderManager);
    }
}
