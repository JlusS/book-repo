package bookrepo.service;

import static bookrepo.util.TestUtil.createCategoryDtoWithoutId;
import static bookrepo.util.TestUtil.createJavaCategory;
import static bookrepo.util.TestUtil.createJavaCategoryDto;
import static bookrepo.util.TestUtil.createProgrammingCategory;
import static bookrepo.util.TestUtil.createProgrammingCategoryDto;
import static bookrepo.util.TestUtil.createUpdatedCategoryDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bookrepo.dto.category.CategoryDto;
import bookrepo.exception.EntityNotFoundException;
import bookrepo.mapper.CategoryMapper;
import bookrepo.model.Category;
import bookrepo.repository.category.CategoryRepository;
import bookrepo.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    private static final Long EXISTING_CATEGORY_ID = 1L;
    private static final Long ANOTHER_CATEGORY_ID = 2L;
    private static final Long NON_EXISTING_CATEGORY_ID = 999L;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Should return page of CategoryDto when findAll is called with valid pageable")
    void findAll_withValidPageable_returnsPageOfCategoryDto() {
        // Given
        Category category1 = createProgrammingCategory();
        Category category2 = createJavaCategory();

        CategoryDto dto1 = createProgrammingCategoryDto();
        CategoryDto dto2 = createJavaCategoryDto();

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category1)).thenReturn(dto1);
        when(categoryMapper.toDto(category2)).thenReturn(dto2);

        // When
        Page<CategoryDto> result = categoryService.findAll(pageable);

        // Then
        assertEquals(2, result.getTotalElements());
        assertEquals("Programming", result.getContent().get(0).getName());
        assertEquals("Java", result.getContent().get(1).getName());

        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDto(category1);
        verify(categoryMapper).toDto(category2);
    }

    @Test
    @DisplayName("Should return CategoryDto when getById is called with existing ID")
    void getById_withValidId_returnsCategoryDto() {
        // Given
        Category category = createProgrammingCategory();
        CategoryDto dto = createProgrammingCategoryDto();

        when(categoryRepository.findById(EXISTING_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(dto);

        // When
        CategoryDto result = categoryService.getById(EXISTING_CATEGORY_ID);

        // Then
        assertEquals("Programming", result.getName());

        verify(categoryRepository).findById(EXISTING_CATEGORY_ID);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when getById is called with non-existing ID")
    void getById_withInvalidId_throwsException() {
        // Given
        when(categoryRepository.findById(NON_EXISTING_CATEGORY_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () ->
                categoryService.getById(NON_EXISTING_CATEGORY_ID));

        verify(categoryRepository).findById(NON_EXISTING_CATEGORY_ID);
    }

    @Test
    @DisplayName("Should save category and return CategoryDto when save is called with valid DTO")
    void save_withValidDto_returnsSavedCategoryDto() {
        // Given
        CategoryDto dto = createCategoryDtoWithoutId();
        Category category = createProgrammingCategory();

        when(categoryMapper.toEntity(dto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(dto);

        // When
        CategoryDto result = categoryService.save(dto);

        // Then
        assertEquals("Programming", result.getName());

        verify(categoryMapper).toEntity(dto);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("""
             Should update category and return updated\s
             CategoryDto when update is called with valid ID and DTO
            \s""")
    void update_withValidIdAndDto_returnsUpdatedCategoryDto() {
        // Given
        CategoryDto dto = createUpdatedCategoryDto();
        Category category = createProgrammingCategory();

        when(categoryRepository.findById(EXISTING_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(dto);

        // When
        CategoryDto result = categoryService.update(EXISTING_CATEGORY_ID, dto);

        // Then
        assertEquals("Updated", result.getName());

        verify(categoryRepository).findById(EXISTING_CATEGORY_ID);
        verify(categoryMapper).updateEntityFromDto(dto, category);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("""
             Should throw EntityNotFoundException\s
             when update is called with non-existing ID
            \s""")
    void update_withInvalidId_throwsException() {
        // Given
        CategoryDto dto = createUpdatedCategoryDto();

        when(categoryRepository.findById(NON_EXISTING_CATEGORY_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () ->
                categoryService.update(NON_EXISTING_CATEGORY_ID, dto));

        verify(categoryRepository).findById(NON_EXISTING_CATEGORY_ID);
    }
}
