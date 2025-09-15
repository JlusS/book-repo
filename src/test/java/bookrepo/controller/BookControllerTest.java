package bookrepo.controller;

import static bookrepo.util.TestUtil.createBookEffectiveJava;
import static bookrepo.util.TestUtil.createBookRequestDto;
import static bookrepo.util.TestUtil.createExpectedBookDto;
import static bookrepo.util.TestUtil.createJavaCategory;
import static bookrepo.util.TestUtil.createProgrammingCategory;
import static bookrepo.util.TestUtil.createValidBookRequestDto;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bookrepo.dto.book.BookDto;
import bookrepo.dto.book.BookDtoWithoutCategoryIds;
import bookrepo.dto.book.CreateBookRequestDto;
import bookrepo.model.Book;
import bookrepo.model.Category;
import bookrepo.repository.book.BookRepository;
import bookrepo.repository.category.CategoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get all books")
    void findAll_WithUserRole_ShouldReturnPageOfBooks() throws Exception {
        // Given
        Category categoryProgramming = categoryRepository.save(createProgrammingCategory());
        Category categoryJava = categoryRepository.save(createJavaCategory());
        Book book = createBookEffectiveJava();
        book.setCategories(Set.of(categoryProgramming, categoryJava));
        bookRepository.save(book);

        BookDto expectedDto = createExpectedBookDto(book);

        // When
        MvcResult result = mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String responseContent = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(responseContent);
        List<BookDto> books = objectMapper.readValue(
                root.get("content").toString(),
                new TypeReference<List<BookDto>>() {
                }
        );

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Effective Java");
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get book by ID")
    void getBookById_WithUserRole_ShouldReturnBook() throws Exception {
        // Given
        Category category = categoryRepository.save(createProgrammingCategory());
        Book book = createBookEffectiveJava();
        book.setCategories(Set.of(category));
        Book savedBook = bookRepository.save(book);

        // When
        MvcResult result = mockMvc.perform(get("/books/{id}", savedBook.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );
        assertNotNull(actual);
        assertEquals(savedBook.getTitle(), actual.getTitle());
        assertEquals(savedBook.getAuthor(), actual.getAuthor());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Search books")
    void searchBooks_WithUserRole_ShouldReturnBooks() throws Exception {
        // Given
        bookRepository.save(createBookEffectiveJava());

        // When
        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("titles", "Effective Java")
                        .param("authors", "Joshua Bloch"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto[] books = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto[].class
        );
        assertNotNull(books);
        assertEquals(1, books.length);
        assertEquals("Effective Java", books[0].getTitle());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Create a new book")
    void createBook_ValidRequestDto_Success() throws Exception {
        // Given
        Category categoryProgramming = categoryRepository.save(createProgrammingCategory());
        Category categoryJava = categoryRepository.save(createJavaCategory());
        CreateBookRequestDto requestDto = createValidBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );
        assertNotNull(actual);
        assertEquals(requestDto.getTitle(), actual.getTitle());
        assertEquals(requestDto.getAuthor(), actual.getAuthor());
        assertEquals(1, bookRepository.count());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Delete book by ID")
    void deleteBookById_WithAdminRole_ShouldDeleteBook() throws Exception {
        // Given
        Book book = bookRepository.save(createBookEffectiveJava());

        // When
        mockMvc.perform(delete("/books/{id}", book.getId()))
                .andExpect(status().isNoContent());

        // Then
        assertEquals(0, bookRepository.count());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Update book")
    void updateBook_WithAdminRole_ShouldUpdateBook() throws Exception {
        // Given
        Book book = bookRepository.save(createBookEffectiveJava());
        CreateBookRequestDto requestDto = createBookRequestDto();
        requestDto.setTitle("Updated Title");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        put("/books/{id}", book.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );
        assertNotNull(actual);
        assertEquals("Updated Title", actual.getTitle());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Create book with USER role should return forbidden")
    void createBook_WithUserRole_ShouldReturnForbidden() throws Exception {
        CreateBookRequestDto requestDto = createBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Delete book with USER role should return forbidden")
    void deleteBook_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/books/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Access without authentication should return unauthorized")
    void accessWithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get books by category ID")
    void getBooksByCategoryId_WithUserRole_ShouldReturnBooks() throws Exception {
        // Given
        Category category = categoryRepository.save(createProgrammingCategory());
        Book book = createBookEffectiveJava();
        book.setCategories(Set.of(category));
        bookRepository.save(book);

        // When
        MvcResult result = mockMvc.perform(get("/categories/{id}/books", category.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDtoWithoutCategoryIds[] books = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDtoWithoutCategoryIds[].class
        );
        assertNotNull(books);
        assertEquals(1, books.length);
        assertEquals(book.getTitle(), books[0].getTitle());
    }
}
