package bookrepo.service;

import bookrepo.dto.category.CategoryDto;
import bookrepo.exception.EntityNotFoundException;
import bookrepo.mapper.CategoryMapper;
import bookrepo.model.Category;
import bookrepo.repository.category.CategoryRepository;
import bookrepo.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
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

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Should return page of CategoryDto when findAll is called with valid pageable")
    void findAll_withValidPageable_returnsPageOfCategoryDto() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Programming");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Java");

        CategoryDto dto1 = new CategoryDto();
        dto1.setId(category1.getId());
        dto1.setName(category1.getName());

        CategoryDto dto2 = new CategoryDto();
        dto2.setId(category2.getId());
        dto2.setName(category2.getName());

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when(categoryMapper.toDto(category1)).thenReturn(dto1);
        Mockito.when(categoryMapper.toDto(category2)).thenReturn(dto2);

        Page<CategoryDto> result = categoryService.findAll(pageable);

        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals("Programming", result.getContent().get(0).getName());
        Assertions.assertEquals("Java", result.getContent().get(1).getName());
    }

    @Test
    @DisplayName("Should return CategoryDto when getById is called with existing ID")
    void getById_withValidId_returnsCategoryDto() {
        Long id = 1L;
        Category category = new Category();
        category.setId(id);
        category.setName("Programming");

        CategoryDto dto = new CategoryDto();
        dto.setId(id);
        dto.setName("Programming");

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(category)).thenReturn(dto);

        CategoryDto result = categoryService.getById(id);

        Assertions.assertEquals("Programming", result.getName());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when getById is called with non-existing ID")
    void getById_withInvalidId_throwsException() {
        Long id = 999L;
        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> categoryService.getById(id));
    }

    @Test
    @DisplayName("Should save category and return CategoryDto when save is called with valid DTO")
    void save_withValidDto_returnsSavedCategoryDto() {
        CategoryDto dto = new CategoryDto();
        dto.setName("Programming");

        Category category = new Category();
        category.setName("Programming");

        Mockito.when(categoryMapper.toEntity(dto)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(dto);

        CategoryDto result = categoryService.save(dto);

        Assertions.assertEquals("Programming", result.getName());
    }

    @Test
    @DisplayName("""
             Should update category and return updated\s
             CategoryDto when update is called with valid ID and DTO
            \s""")
    void update_withValidIdAndDto_returnsUpdatedCategoryDto() {
        Long id = 1L;
        CategoryDto dto = new CategoryDto();
        dto.setName("Updated");

        Category category = new Category();
        category.setId(id);
        category.setName("Old");

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        Mockito.doAnswer(invocation -> {
            category.setName(dto.getName());
            return null;
        }).when(categoryMapper).updateEntityFromDto(dto, category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(dto);

        CategoryDto result = categoryService.update(id, dto);

        Assertions.assertEquals("Updated", result.getName());
    }

    @Test
    @DisplayName("""
             Should throw EntityNotFoundException\s
             when update is called with non-existing ID
            \s""")
    void update_withInvalidId_throwsException() {
        Long id = 999L;
        CategoryDto dto = new CategoryDto();
        dto.setName("New");

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                categoryService.update(id, dto));
    }
}
