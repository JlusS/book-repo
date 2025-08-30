package bookrepo.mapper;

import bookrepo.config.MapperConfig;
import bookrepo.dto.category.CategoryDto;
import bookrepo.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CategoryDto categoryDto, @MappingTarget Category category);
}
