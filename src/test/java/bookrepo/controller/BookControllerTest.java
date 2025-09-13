package bookrepo.controller;

import bookrepo.dto.book.BookDto;
import bookrepo.dto.book.BookDtoWithoutCategoryIds;
import bookrepo.dto.book.BookSearchParameters;
import bookrepo.dto.book.CreateBookRequestDto;
import bookrepo.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get all books")
    void findAll_WithUserRole_ShouldReturnPageOfBooks() throws Exception {
        // Given
        BookDto bookDto = createBookDto();
        Page<BookDto> page = new PageImpl<>(List.of(bookDto));
        when(bookService.findAll(any(Pageable.class))).thenReturn(page);

        // When
        MvcResult result = mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        Assertions.assertTrue(responseContent.contains("Test Book"));
        Assertions.assertTrue(responseContent.contains("Test Author"));
        Assertions.assertTrue(responseContent.contains("29.99"));

        Assertions.assertTrue(responseContent.contains("\"title\":\"Test Book\""));
        Assertions.assertTrue(responseContent.contains("\"author\":\"Test Author\""));
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get book by ID")
    void getBookById_WithUserRole_ShouldReturnBook() throws Exception {
        // Given
        BookDto expected = createBookDto();
        when(bookService.getById(1L)).thenReturn(expected);

        // When
        MvcResult result = mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getAuthor(), actual.getAuthor());
        Assertions.assertEquals(expected.getPrice(), actual.getPrice());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Search books")
    void searchBooks_WithUserRole_ShouldReturnBooks() throws Exception {
        // Given
        BookDto bookDto = createBookDto();
        when(bookService.search(any(BookSearchParameters.class))).thenReturn(List.of(bookDto));

        // When
        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("titles", "Test")
                        .param("authors", "Author"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto[] books = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto[].class);
        Assertions.assertNotNull(books);
        Assertions.assertEquals(1, books.length);
        Assertions.assertEquals("Test Book", books[0].getTitle());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Create a new book")
    void createBook_ValidRequestDto_Success() throws Exception {
        // Given
        CreateBookRequestDto requestDto = createBookRequestDto();
        BookDto expected = createBookDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        when(bookService.save(any(CreateBookRequestDto.class))).thenReturn(expected);

        // When
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getAuthor(), actual.getAuthor());
        Assertions.assertEquals(expected.getPrice(), actual.getPrice());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Delete book by ID")
    void deleteBookById_WithAdminRole_ShouldDeleteBook() throws Exception {
        // When & Then
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService).deleteById(1L);
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Update book")
    void updateBook_WithAdminRole_ShouldUpdateBook() throws Exception {
        // Given
        CreateBookRequestDto requestDto = createBookRequestDto();
        BookDto expected = createBookDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        when(bookService.update(eq(1L), any(CreateBookRequestDto.class))).thenReturn(expected);

        // When
        MvcResult result = mockMvc.perform(
                        put("/books/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getAuthor(), actual.getAuthor());
        Assertions.assertEquals(expected.getPrice(), actual.getPrice());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Create book with USER role should return forbidden")
    void createBook_WithUserRole_ShouldReturnForbidden() throws Exception {
        // Given
        CreateBookRequestDto requestDto = createBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When & Then
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
        // When & Then
        mockMvc.perform(delete("/books/1"))
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
        BookDtoWithoutCategoryIds bookDto = createBookDtoWithoutCategoryIds();
        when(bookService.findAllByCategoryId(1L)).thenReturn(List.of(bookDto));

        // When
        MvcResult result = mockMvc.perform(get("/categories/1/books"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDtoWithoutCategoryIds[] books = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDtoWithoutCategoryIds[].class
        );
        Assertions.assertNotNull(books);
        Assertions.assertEquals(1, books.length);
        Assertions.assertEquals("Test Book", books[0].getTitle());
    }

    private BookDto createBookDto() {
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

    private BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds() {
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

    private CreateBookRequestDto createBookRequestDto() {
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
}