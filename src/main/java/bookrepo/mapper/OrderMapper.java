package bookrepo.mapper;

import bookrepo.config.MapperConfig;
import bookrepo.dto.order.OrderDto;
import bookrepo.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order order);
}
