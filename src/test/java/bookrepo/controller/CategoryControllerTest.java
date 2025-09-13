package bookrepo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bookrepo.dto.category.CategoryDto;
import bookrepo.service.BookService;
import bookrepo.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private BookService bookService;

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get all categories")
    void getAll_WithUserRole_ShouldReturnCategories() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get category by ID")
    void getCategoryById_WithUserRole_ShouldReturnCategory() throws Exception {
        // Given
        CategoryDto expected = new CategoryDto();
        expected.setId(1L);
        expected.setName("Fiction");
        expected.setDescription("Fiction books");

        when(categoryService.getById(1L)).thenReturn(expected);

        // When
        MvcResult result = mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Create a new category")
    void createCategory_ValidRequestDto_Success() throws Exception {
        // Given
        CategoryDto requestDto = new CategoryDto();
        requestDto.setName("Science");
        requestDto.setDescription("Science books");

        CategoryDto expected = new CategoryDto();
        expected.setId(1L);
        expected.setName("Science");
        expected.setDescription("Science books");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        when(categoryService.save(any(CategoryDto.class))).thenReturn(expected);

        // When
        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        CategoryDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Update category")
    void updateCategory_ValidRequestDto_Success() throws Exception {
        // Given
        CategoryDto requestDto = new CategoryDto();
        requestDto.setName("Updated Fiction");
        requestDto.setDescription("Updated description");

        CategoryDto expected = new CategoryDto();
        expected.setId(1L);
        expected.setName("Updated Fiction");
        expected.setDescription("Updated description");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        when(categoryService.update(eq(1L), any(CategoryDto.class))).thenReturn(expected);

        // When
        MvcResult result = mockMvc.perform(
                        put("/categories/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Delete category")
    void deleteCategory_WithAdminRole_ShouldDelete() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get books by category ID")
    void getBooksByCategoryId_WithUserRole_ShouldReturnBooks() throws Exception {
        mockMvc.perform(get("/categories/1/books"))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Create category with USER role should return forbidden")
    void createCategory_WithUserRole_ShouldReturnForbidden() throws Exception {
        CategoryDto requestDto = new CategoryDto();
        requestDto.setName("Test");
        requestDto.setDescription("Test category");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Access without authentication should return unauthorized")
    void accessWithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isUnauthorized());
    }
}
