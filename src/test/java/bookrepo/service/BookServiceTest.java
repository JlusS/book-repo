package bookrepo.service;

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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
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
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Effective Java");
        requestDto.setAuthor("Joshua Bloch");
        requestDto.setDescription("A comprehensive guide to best practices in Java programming.");
        requestDto.setPrice(BigDecimal.valueOf(45.00));
        requestDto.setIsbn("978-0134686097");
        requestDto.setCategoryIds(Set.of(1L, 2L));

        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setDescription(requestDto.getDescription());
        book.setPrice(requestDto.getPrice());
        book.setIsbn(requestDto.getIsbn());

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Programming");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Java");

        Set<Category> categories = Set.of(category1, category2);
        book.setCategories(categories);

        BookDto expectedDto = new BookDto();
        expectedDto.setTitle(book.getTitle());
        expectedDto.setAuthor(book.getAuthor());
        expectedDto.setDescription(book.getDescription());
        expectedDto.setPrice(book.getPrice());
        expectedDto.setIsbn(book.getIsbn());
        expectedDto.setCategoryIds(Set.of(1L, 2L));

        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(book);
        Mockito.when(categoryRepository.findAllById(requestDto.getCategoryIds()))
                .thenReturn(new ArrayList<>(categories));
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(expectedDto);

        BookDto actualDto = bookService.save(requestDto);
        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("""
            Should show all books when findAll is called with valid pageable
            and return the corresponding Page of BookDto
            """)
    void findAll_withValidPageable_returnsPageOfBookDto() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Effective Java");
        book1.setAuthor("Joshua Bloch");
        book1.setDescription("A comprehensive guide to best practices in Java programming.");
        book1.setPrice(BigDecimal.valueOf(45.00));
        book1.setIsbn("978-0134686097");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Clean Code");
        book2.setAuthor("Robert C. Martin");
        book2.setDescription("A Handbook of Agile Software Craftsmanship.");
        book2.setPrice(BigDecimal.valueOf(40.00));
        book2.setIsbn("978-0132350884");

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book1, book2);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        Mockito.when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        Mockito.when(bookMapper.toDto(book1)).thenReturn(new BookDto() {
            {
                setId(book1.getId());
                setTitle(book1.getTitle());
                setAuthor(book1.getAuthor());
                setDescription(book1.getDescription());
                setPrice(book1.getPrice());
                setIsbn(book1.getIsbn());
            }
        });
        Mockito.when(bookMapper.toDto(book2)).thenReturn(new BookDto() {
            {
                setId(book2.getId());
                setTitle(book2.getTitle());
                setAuthor(book2.getAuthor());
                setDescription(book2.getDescription());
                setPrice(book2.getPrice());
                setIsbn(book2.getIsbn());
            }
        });

        Page<BookDto> actualPage = bookService.findAll(pageable);
        Assertions.assertEquals(2, actualPage.getTotalElements());
        Assertions.assertEquals(1, actualPage.getTotalPages());
        Assertions.assertEquals(2, actualPage.getContent().size());
        Assertions.assertEquals("Effective Java", actualPage.getContent().get(0).getTitle());
        Assertions.assertEquals("Clean Code", actualPage.getContent().get(1).getTitle());
    }

    @Test
    @DisplayName("""
            Should return BookDto when getById is called with existing ID
            """)
    void getById_withValidId_returnsBookDto() {
        Long id = 1L;
        Book book = new Book();
        book.setId(id);
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");

        BookDto expectedDto = new BookDto();
        expectedDto.setId(id);
        expectedDto.setTitle(book.getTitle());
        expectedDto.setAuthor(book.getAuthor());

        Mockito.when(bookRepository.findById(id)).thenReturn(java.util.Optional.of(book));
        Mockito.when(bookMapper.toDto(book)).thenReturn(expectedDto);

        BookDto actualDto = bookService.getById(id);
        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("""
            Should update book when update is called with valid ID and request DTO
            and return updated BookDto
            """)
    void update_withValidIdAndRequest_returnsUpdatedBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Updated Title");
        requestDto.setAuthor("Updated Author");
        requestDto.setCategoryIds(Set.of(1L));

        Long id = 1L;
        Book existingBook = new Book();
        existingBook.setId(id);
        existingBook.setTitle("Old Title");

        Category category = new Category();
        category.setId(1L);
        category.setName("Programming");

        Set<Category> categories = Set.of(category);
        existingBook.setCategories(categories);

        BookDto expectedDto = new BookDto();
        expectedDto.setId(id);
        expectedDto.setTitle(requestDto.getTitle());
        expectedDto.setAuthor(requestDto.getAuthor());
        expectedDto.setCategoryIds(Set.of(1L));

        Mockito.when(bookRepository.findById(id)).thenReturn(java.util.Optional.of(existingBook));
        Mockito.doAnswer(invocation -> {
            existingBook.setTitle(requestDto.getTitle());
            existingBook.setAuthor(requestDto.getAuthor());
            return null;
        }).when(bookMapper).updateModelFromDto(requestDto, existingBook);
        Mockito.when(categoryRepository.findAllById(requestDto.getCategoryIds()))
                .thenReturn(new ArrayList<>(categories));
        Mockito.when(bookRepository.save(existingBook)).thenReturn(existingBook);
        Mockito.when(bookMapper.toDto(existingBook)).thenReturn(expectedDto);

        BookDto actualDto = bookService.update(id, requestDto);
        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("""
            Should return list of BookDto when search is called with valid parameters
            """)
    void search_withValidParams_returnsListOfBookDto() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Effective Java");

        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());

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

        Mockito.when(bookSpecificationBuilder.build(params)).thenReturn(spec);
        Mockito.when(bookRepository.findAll(spec)).thenReturn(books);
        Mockito.when(bookMapper.toDto(book)).thenReturn(dto);

        List<BookDto> result = bookService.search(params);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Effective Java", result.get(0).getTitle());
    }

    @Test
    @DisplayName("""
             Should return list of BookDtoWithoutCategoryIds\s
             when findAllByCategoryId is called with valid ID
            \s""")
    void findAllByCategoryId_withValidId_returnsListOfBookDtoWithoutCategoryIds() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Programming");

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Effective Java");

        BookDtoWithoutCategoryIds dto = new BookDtoWithoutCategoryIds();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());

        Mockito.when(categoryRepository.findById(categoryId))
                .thenReturn(java.util.Optional.of(category));
        Mockito.when(bookRepository.findAllByCategories_Id(categoryId))
                .thenReturn(List.of(book));
        Mockito.when(bookMapper.toDtoWithoutCategories(book))
                .thenReturn(dto);

        List<BookDtoWithoutCategoryIds> result = bookService.findAllByCategoryId(categoryId);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Effective Java", result.get(0).getTitle());
    }
}
