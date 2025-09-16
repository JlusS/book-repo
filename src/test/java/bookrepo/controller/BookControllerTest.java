package bookrepo.controller;

import static bookrepo.util.TestUtil.createBookRequestDto;
import static bookrepo.util.TestUtil.createValidBookRequestDto;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bookrepo.dto.book.BookDto;
import bookrepo.dto.book.BookDtoWithoutCategoryIds;
import bookrepo.dto.book.CreateBookRequestDto;
import bookrepo.mapper.BookMapper;
import bookrepo.model.Book;
import bookrepo.model.Category;
import bookrepo.repository.book.BookRepository;
import bookrepo.repository.category.CategoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
        "classpath:database/delete-data-from-tables.sql",
        "classpath:database/book/add-books-to-table.sql",
        "classpath:database/category/add-categories-to-category-table.sql",
        "classpath:database/add-categories-to-books.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/delete-data-from-tables.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookMapper bookMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterAll
    static void afterAll(@Autowired BookRepository bookRepository,
                         @Autowired CategoryRepository categoryRepository) {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get all books")
    void findAll_WithUserRole_ShouldReturnPageOfBooks() throws Exception {
        // Given
        BookDto expectedDto = bookRepository.findByIdWithCategories(1L)
                .map(bookMapper::toDto)
                .orElseThrow();

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

        assertThat(books).isNotEmpty();
        BookDto actualDto = books.get(0);

        assertThat(actualDto.getId()).isEqualTo(expectedDto.getId());
        assertThat(actualDto.getTitle()).isEqualTo("Effective Java");
        assertThat(actualDto.getAuthor()).isEqualTo("Joshua Bloch");
        assertThat(actualDto.getIsbn()).isEqualTo("9780134685991");
        assertThat(actualDto.getPrice()).isEqualTo(BigDecimal.valueOf(49.99));
        assertThat(actualDto.getCategoryIds()).containsExactlyInAnyOrder(1L, 2L);
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get book by ID")
    void getBookById_WithUserRole_ShouldReturnBook() throws Exception {
        // Given
        BookDto expectedDto = bookRepository.findByIdWithCategories(1L)
                .map(bookMapper::toDto)
                .orElseThrow();

        // When
        MvcResult result = mockMvc.perform(get("/books/{id}", expectedDto.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );
        assertEquals(expectedDto, actualDto);
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Search books")
    void searchBooks_WithUserRole_ShouldReturnBooks() throws Exception {
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
        long initialCount = bookRepository.count();
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
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id", "categoryIds")
                .isEqualTo(bookMapper.toDto(bookMapper.toModel(requestDto)));

    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Delete book by ID")
    void deleteBookById_WithAdminRole_ShouldDeleteBook() throws Exception {
        // Given
        long initialCount = bookRepository.count();
        Book book = bookRepository.findAll().get(0);

        // When
        mockMvc.perform(delete("/books/{id}", book.getId()))
                .andExpect(status().isNoContent());

        // Then
        assertEquals(initialCount - 1, bookRepository.count());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Update book")
    void updateBook_WithAdminRole_ShouldUpdateBook() throws Exception {
        // Given
        Book book = bookRepository.findAll().get(0);
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
        Category category = categoryRepository.findAll().get(0);

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
        assertEquals("Effective Java", books[0].getTitle());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get books by category ID - should return books for valid category")
    void getBooksByCategoryId_WithValidCategory_ShouldReturnBooks() throws Exception {
        // Given
        Category category = categoryRepository.findAll().get(0);
        Book expectedBook = bookRepository.findAll().get(0);
        BookDtoWithoutCategoryIds expectedDto = bookMapper.toDtoWithoutCategories(expectedBook);

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

        assertThat(books[0]).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get books by invalid category ID - should return bad request")
    void getBooksByCategoryId_WithInvalidCategoryId_ShouldReturnBadRequest() throws Exception {
        // Given
        String invalidCategoryId = "invalid";

        // When & Then
        mockMvc.perform(get("/categories/{id}/books", invalidCategoryId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get books by category ID without authentication - should return unauthorized")
    void getBooksByCategoryId_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Given
        Category category = categoryRepository.findAll().get(0);

        // When & Then
        mockMvc.perform(get("/categories/{id}/books", category.getId()))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get books by different categories - should return correct books")
    void getBooksByCategoryId_WithDifferentCategories_ShouldReturnCorrectBooks() throws Exception {
        // Given
        Category programmingCategory = categoryRepository.findById(1L).orElseThrow();
        Category javaCategory = categoryRepository.findById(2L).orElseThrow();

        // When & Then
        MvcResult result1 = mockMvc.perform(get("/categories/{id}/books",
                        programmingCategory.getId()))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result2 = mockMvc.perform(get("/categories/{id}/books",
                        javaCategory.getId()))
                .andExpect(status().isOk())
                .andReturn();

        BookDtoWithoutCategoryIds[] books1 = objectMapper.readValue(
                result1.getResponse().getContentAsString(),
                BookDtoWithoutCategoryIds[].class
        );

        BookDtoWithoutCategoryIds[] books2 = objectMapper.readValue(
                result2.getResponse().getContentAsString(),
                BookDtoWithoutCategoryIds[].class
        );

        assertNotNull(books1);
        assertNotNull(books2);
        assertEquals(1, books1.length);
        assertEquals(1, books2.length);

        Book expectedBook = bookRepository.findById(1L).orElseThrow();
        BookDtoWithoutCategoryIds expectedDto = bookMapper.toDtoWithoutCategories(expectedBook);

        assertThat(books1[0]).usingRecursiveComparison().isEqualTo(expectedDto);
        assertThat(books2[0]).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get books by category ID with pagination - should return paged results")
    void getBooksByCategoryId_WithPagination_ShouldReturnPagedResults() throws Exception {
        // Given
        Category category = categoryRepository.findAll().get(0);

        // When
        MvcResult result = mockMvc.perform(get("/categories/{id}/books", category.getId())
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDtoWithoutCategoryIds[] books = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDtoWithoutCategoryIds[].class
        );

        assertNotNull(books);
        assertEquals(1, books.length);
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get book by ID - should return exact book")
    void getBookById_WithUserRole_ShouldReturnExactBook() throws Exception {
        // Given
        BookDto expectedDto = bookRepository.findByIdWithCategories(1L)
                .map(bookMapper::toDto)
                .orElseThrow();

        // When
        MvcResult result = mockMvc.perform(get("/books/{id}", expectedDto.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );

        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get all books - should return exact books list")
    void findAll_WithUserRole_ShouldReturnExactBooks() throws Exception {
        // Given
        BookDto expectedDto = bookRepository.findByIdWithCategories(1L)
                .map(bookMapper::toDto)
                .orElseThrow();

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

        assertThat(books.get(0)).usingRecursiveComparison().isEqualTo(expectedDto);
    }
}
