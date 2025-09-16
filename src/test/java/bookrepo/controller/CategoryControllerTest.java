package bookrepo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bookrepo.dto.category.CategoryDto;
import bookrepo.mapper.CategoryMapper;
import bookrepo.model.Category;
import bookrepo.repository.category.CategoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        "classpath:database/category/add-categories-to-category-table.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/delete-data-from-tables.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CategoryControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterAll
    static void afterAll(@Autowired CategoryRepository categoryRepository) {
        categoryRepository.deleteAll();
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get all categories")
    void getAll_WithUserRole_ShouldReturnCategories() throws Exception {
        // Given
        List<Category> categories = categoryRepository.findAll();
        CategoryDto expectedDto = categoryMapper.toDto(categories.get(0));

        // When
        MvcResult result = mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<CategoryDto> actual = objectMapper.readValue(
                root.get("content").toString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CategoryDto.class)
        );
        assertThat(actual).isNotEmpty();
        assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get category by ID")
    void getCategoryById_WithUserRole_ShouldReturnCategory() throws Exception {
        // Given
        Category category = categoryRepository.findAll().get(0);
        CategoryDto expectedDto = categoryMapper.toDto(category);

        // When
        MvcResult result = mockMvc.perform(get("/categories/{id}", category.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );
        assertNotNull(actualDto);
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Create a new category")
    void createCategory_ValidRequestDto_Success() throws Exception {
        // Given
        CategoryDto requestDto = new CategoryDto();
        requestDto.setName("Science");
        requestDto.setDescription("Science books");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(requestDto.getName(), actual.getName());
        assertEquals(requestDto.getDescription(), actual.getDescription());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Update category")
    void updateCategory_ValidRequestDto_Success() throws Exception {
        // Given
        Category category = categoryRepository.findAll().get(0);
        CategoryDto requestDto = new CategoryDto();
        requestDto.setName("Updated Fiction");
        requestDto.setDescription("Updated description");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        put("/categories/{id}", category.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );
        assertNotNull(actual);
        assertEquals(requestDto.getName(), actual.getName());
        assertEquals(requestDto.getDescription(), actual.getDescription());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Delete category")
    void deleteCategory_WithAdminRole_ShouldDelete() throws Exception {
        // Given
        Category category = categoryRepository.findAll().get(0);
        long initialCount = categoryRepository.count();

        // When
        mockMvc.perform(delete("/categories/{id}", category.getId()))
                .andExpect(status().isNoContent());

        // Then
        assertEquals(initialCount - 1, categoryRepository.count());
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
