package bookrepo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bookrepo.dto.book.BookDto;
import bookrepo.dto.book.BookDtoWithoutCategoryIds;
import bookrepo.dto.book.BookSearchParameters;
import bookrepo.dto.book.CreateBookRequestDto;
import bookrepo.mapper.BookMapper;
import bookrepo.model.Book;
import bookrepo.model.Category;
import bookrepo.repository.book.BookRepository;
import bookrepo.repository.book.BookSpecificationBuilder;
import bookrepo.repository.category.CategoryRepository;
import bookrepo.service.impl.BookServiceImpl;
import bookrepo.util.TestUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("""
            Should save a book when save is called with valid request DTO
            and return the corresponding BookDto with correct category IDs
            """)
    void save_withValidRequest_returnsBookDtoWithCategoryIds() {
        // Given
        CreateBookRequestDto requestDto = TestUtil.createValidBookRequestDto();
        Book book = TestUtil.createBookFromRequest(requestDto);

        Category categoryProgramming = TestUtil.createProgrammingCategory();
        Category categoryJava = TestUtil.createJavaCategory();
        Set<Category> categories = Set.of(categoryProgramming, categoryJava);
        book.setCategories(categories);

        BookDto expectedDto = TestUtil.createExpectedBookDto(book);

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(categoryRepository.findAllById(requestDto.getCategoryIds()))
                .thenReturn(new ArrayList<>(categories));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        // When
        BookDto actualDto = bookService.save(requestDto);

        // Then
        assertEquals(expectedDto, actualDto);
        verify(bookMapper).toModel(requestDto);
        verify(categoryRepository).findAllById(requestDto.getCategoryIds());
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("""
            Should show all books when findAll is called with valid pageable
            and return the corresponding Page of BookDto
            """)
    void findAll_withValidPageable_returnsPageOfBookDto() {
        // Given
        Book book1 = TestUtil.createBookEffectiveJava();
        Book book2 = TestUtil.createBookCleanCode();

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book1, book2);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        BookDto dto1 = TestUtil.createBookDtoEffectiveJava(book1);
        BookDto dto2 = TestUtil.createBookDtoCleanCode(book2);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book1)).thenReturn(dto1);
        when(bookMapper.toDto(book2)).thenReturn(dto2);

        // When
        Page<BookDto> actualPage = bookService.findAll(pageable);

        // Then
        assertEquals(2, actualPage.getTotalElements());
        assertEquals(1, actualPage.getTotalPages());
        assertEquals(2, actualPage.getContent().size());
        assertEquals("Effective Java", actualPage.getContent().get(0).getTitle());
        assertEquals("Clean Code", actualPage.getContent().get(1).getTitle());

        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(book1);
        verify(bookMapper).toDto(book2);
    }

    @Test
    @DisplayName("""
            Should return BookDto when getById is called with existing ID
            """)
    void getById_withValidId_returnsBookDto() {
        // Given
        Long id = 1L;
        Book book = TestUtil.createBookEffectiveJava();
        BookDto expectedDto = TestUtil.createBookDtoEffectiveJava(book);

        when(bookRepository.findById(id)).thenReturn(java.util.Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        // When
        BookDto actualDto = bookService.getById(id);

        // Then
        assertEquals(expectedDto, actualDto);
        verify(bookRepository).findById(id);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("""
            Should update book when update is called with valid ID and request DTO
            and return updated BookDto
            """)
    void update_withValidIdAndRequest_returnsUpdatedBookDto() {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Updated Title");
        requestDto.setAuthor("Updated Author");
        requestDto.setCategoryIds(Set.of(1L));

        Long id = 1L;
        Book existingBook = TestUtil.createBookEffectiveJava();
        existingBook.setTitle("Old Title");

        Category category = TestUtil.createProgrammingCategory();
        Set<Category> categories = Set.of(category);
        existingBook.setCategories(categories);

        BookDto expectedDto = new BookDto();
        expectedDto.setId(id);
        expectedDto.setTitle(requestDto.getTitle());
        expectedDto.setAuthor(requestDto.getAuthor());
        expectedDto.setCategoryIds(Set.of(1L));

        when(bookRepository.findById(id)).thenReturn(java.util.Optional.of(existingBook));
        when(categoryRepository.findAllById(requestDto.getCategoryIds()))
                .thenReturn(new ArrayList<>(categories));
        when(bookRepository.save(existingBook)).thenReturn(existingBook);
        when(bookMapper.toDto(existingBook)).thenReturn(expectedDto);

        // When
        BookDto actualDto = bookService.update(id, requestDto);

        // Then
        assertEquals(expectedDto, actualDto);
        verify(bookRepository).findById(id);
        verify(bookMapper).updateModelFromDto(requestDto, existingBook);
        verify(categoryRepository).findAllById(requestDto.getCategoryIds());
        verify(bookRepository).save(existingBook);
        verify(bookMapper).toDto(existingBook);
    }

    @Test
    @DisplayName("""
            Should return list of BookDto when search is called with valid parameters
            """)
    void search_withValidParams_returnsListOfBookDto() {
        // Given
        Book book = TestUtil.createBookEffectiveJava();
        BookDto dto = TestUtil.createBookDtoEffectiveJava(book);
        List<Book> books = List.of(book);

        @SuppressWarnings("unchecked")
        Specification<Book> spec = Mockito.mock(Specification.class);

        BookSearchParameters params = new BookSearchParameters(
                new String[]{"Joshua Bloch"},
                new String[]{"Effective Java"},
                new String[]{"Addison-Wesley"},
                new String[]{"Programming"},
                new String[]{"cover.jpg"},
                new String[]{"45.00"}
        );

        when(bookSpecificationBuilder.build(params)).thenReturn(spec);
        when(bookRepository.findAll(spec)).thenReturn(books);
        when(bookMapper.toDto(book)).thenReturn(dto);

        // When
        List<BookDto> result = bookService.search(params);

        // Then
        assertEquals(1, result.size());
        assertEquals("Effective Java", result.get(0).getTitle());
        verify(bookSpecificationBuilder).build(params);
        verify(bookRepository).findAll(spec);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("""
             Should return list of BookDtoWithoutCategoryIds\s
             when findAllByCategoryId is called with valid ID
            \s""")
    void findAllByCategoryId_withValidId_returnsListOfBookDtoWithoutCategoryIds() {
        // Given
        Long categoryId = 1L;
        Category category = TestUtil.createProgrammingCategory();
        Book book = TestUtil.createBookEffectiveJava();
        BookDtoWithoutCategoryIds dto = TestUtil.createBookDtoWithoutCategories(book);

        when(categoryRepository.findById(categoryId))
                .thenReturn(java.util.Optional.of(category));
        when(bookRepository.findAllByCategories_Id(categoryId))
                .thenReturn(List.of(book));
        when(bookMapper.toDtoWithoutCategories(book))
                .thenReturn(dto);

        // When
        List<BookDtoWithoutCategoryIds> result = bookService.findAllByCategoryId(categoryId);

        // Then
        assertEquals(1, result.size());
        assertEquals("Effective Java", result.get(0).getTitle());
        verify(categoryRepository).findById(categoryId);
        verify(bookRepository).findAllByCategories_Id(categoryId);
        verify(bookMapper).toDtoWithoutCategories(book);
    }
}
