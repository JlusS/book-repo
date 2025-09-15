package bookrepo.util;

import bookrepo.dto.book.BookDto;
import bookrepo.dto.book.BookDtoWithoutCategoryIds;
import bookrepo.dto.book.CreateBookRequestDto;
import bookrepo.dto.category.CategoryDto;
import bookrepo.model.Book;
import bookrepo.model.Category;
import java.math.BigDecimal;
import java.util.Set;

public class TestUtil {

    public static CreateBookRequestDto createValidBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Effective Java");
        requestDto.setAuthor("Joshua Bloch");
        requestDto.setDescription("A comprehensive guide to best practices in Java programming.");
        requestDto.setPrice(BigDecimal.valueOf(45.00));
        requestDto.setIsbn("978-0134686097");
        requestDto.setCategoryIds(Set.of(1L, 2L));
        return requestDto;
    }

    public static Book createBookFromRequest(CreateBookRequestDto requestDto) {
        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setDescription(requestDto.getDescription());
        book.setPrice(requestDto.getPrice());
        book.setIsbn(requestDto.getIsbn());
        return book;
    }

    public static Category createProgrammingCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Programming");
        return category;
    }

    public static Category createJavaCategory() {
        Category category = new Category();
        category.setId(2L);
        category.setName("Java");
        return category;
    }

    public static BookDto createExpectedBookDto(Book book) {
        BookDto expectedDto = new BookDto();
        expectedDto.setTitle(book.getTitle());
        expectedDto.setAuthor(book.getAuthor());
        expectedDto.setDescription(book.getDescription());
        expectedDto.setPrice(book.getPrice());
        expectedDto.setIsbn(book.getIsbn());
        expectedDto.setCategoryIds(Set.of(1L, 2L));
        return expectedDto;
    }

    public static Book createBookEffectiveJava() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setDescription("A comprehensive guide to best practices in Java programming.");
        book.setPrice(BigDecimal.valueOf(45.00));
        book.setIsbn("978-0134686097");
        return book;
    }

    public static Book createBookCleanCode() {
        Book book = new Book();
        book.setId(2L);
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setDescription("A Handbook of Agile Software Craftsmanship.");
        book.setPrice(BigDecimal.valueOf(40.00));
        book.setIsbn("978-0132350884");
        return book;
    }

    public static BookDto createBookDtoEffectiveJava(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setDescription(book.getDescription());
        dto.setPrice(book.getPrice());
        dto.setIsbn(book.getIsbn());
        return dto;
    }

    public static BookDto createBookDtoCleanCode(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setDescription(book.getDescription());
        dto.setPrice(book.getPrice());
        dto.setIsbn(book.getIsbn());
        return dto;
    }

    public static BookDtoWithoutCategoryIds createBookDtoWithoutCategories(Book book) {
        BookDtoWithoutCategoryIds dto = new BookDtoWithoutCategoryIds();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        return dto;
    }

    public static BookDto createBookDto() {
        BookDto dto = new BookDto();
        dto.setId(1L);
        dto.setTitle("Test Book");
        dto.setAuthor("Test Author");
        dto.setIsbn("ISBN123");
        dto.setPrice(BigDecimal.valueOf(29.99));
        dto.setDescription("Test Description");
        dto.setCoverImage("cover.jpg");
        dto.setCategoryIds(Set.of(1L));
        return dto;
    }

    public static BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds() {
        BookDtoWithoutCategoryIds dto = new BookDtoWithoutCategoryIds();
        dto.setId(1L);
        dto.setTitle("Test Book");
        dto.setAuthor("Test Author");
        dto.setIsbn("ISBN123");
        dto.setPrice(BigDecimal.valueOf(29.99));
        dto.setDescription("Test Description");
        dto.setCoverImage("cover.jpg");
        return dto;
    }

    public static CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto dto = new CreateBookRequestDto();
        dto.setTitle("Test Book");
        dto.setAuthor("Test Author");
        dto.setIsbn("ISBN123");
        dto.setPrice(BigDecimal.valueOf(29.99));
        dto.setDescription("Test Description");
        dto.setCoverImage("cover.jpg");
        dto.setCategoryIds(Set.of(1L));
        return dto;
    }

    public static CategoryDto createProgrammingCategoryDto() {
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setName("Programming");
        return dto;
    }

    public static CategoryDto createJavaCategoryDto() {
        CategoryDto dto = new CategoryDto();
        dto.setId(2L);
        dto.setName("Java");
        return dto;
    }

    public static CategoryDto createCategoryDtoWithoutId() {
        CategoryDto dto = new CategoryDto();
        dto.setName("Programming");
        return dto;
    }

    public static CategoryDto createUpdatedCategoryDto() {
        CategoryDto dto = new CategoryDto();
        dto.setName("Updated");
        return dto;
    }
}
