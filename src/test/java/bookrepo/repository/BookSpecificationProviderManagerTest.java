package bookrepo.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bookrepo.model.Book;
import bookrepo.repository.book.BookSpecificationProviderManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookSpecificationProviderManagerTest {

    @Mock
    private SpecificationProvider<Book> authorProvider;

    @Mock
    private SpecificationProvider<Book> titleProvider;

    @InjectMocks
    private BookSpecificationProviderManager bookSpecificationProviderManager;

    @Test
    void getSpecificationProvider_WithExistingKey_ShouldReturnProvider() {
        // Given
        when(authorProvider.getKey()).thenReturn("author");
        BookSpecificationProviderManager manager =
                new BookSpecificationProviderManager(List.of(authorProvider));

        // When
        SpecificationProvider<Book> result = manager.getSpecificationProvider("author");

        // Then
        assertNotNull(result);
        assertEquals(authorProvider, result);
        verify(authorProvider).getKey();
    }

    @Test
    void getSpecificationProvider_WithNonExistingKey_ShouldThrowException() {
        // Given
        when(authorProvider.getKey()).thenReturn("author");
        BookSpecificationProviderManager manager =
                new BookSpecificationProviderManager(List.of(authorProvider));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> manager.getSpecificationProvider("nonexistent"));

        assertEquals("No specification provider found for key: nonexistent",
                exception.getMessage());
    }

    @Test
    void getSpecificationProvider_WithMultipleProviders_ShouldReturnCorrectOne() {
        // Given
        when(authorProvider.getKey()).thenReturn("author");
        when(titleProvider.getKey()).thenReturn("title");

        BookSpecificationProviderManager manager =
                new BookSpecificationProviderManager(List.of(authorProvider, titleProvider));

        // When
        SpecificationProvider<Book> result = manager.getSpecificationProvider("title");

        // Then
        assertNotNull(result);
        assertEquals(titleProvider, result);
        verify(authorProvider).getKey();
        verify(titleProvider).getKey();
    }

    @Test
    void getSpecificationProvider_WithEmptyProviders_ShouldThrowException() {
        // Given
        BookSpecificationProviderManager manager =
                new BookSpecificationProviderManager(List.of());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> manager.getSpecificationProvider("author"));

        assertEquals("No specification provider found for key: author", exception.getMessage());
    }
}
