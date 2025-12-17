package ec.edu.espe.pedido_service.pedido_service.mapper;

import ec.edu.espe.pedido_service.pedido_service.dto.OrderRequestDto;
import ec.edu.espe.pedido_service.pedido_service.dto.OrderResponseDto;
import ec.edu.espe.pedido_service.pedido_service.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public Order toEntity(OrderRequestDto dto) {
        Order order = new Order();
        order.setCustomerName(dto.getCustomerName());
        order.setOrigin(dto.getOrigin());
        order.setDestination(dto.getDestination());
        order.setDeliveryType(dto.getDeliveryType());
        return order;
    }

    public OrderResponseDto toDto(Order entity) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(entity.getId());
        dto.setCustomerName(entity.getCustomerName());
        dto.setOrigin(entity.getOrigin());
        dto.setDestination(entity.getDestination());
        dto.setDeliveryType(entity.getDeliveryType());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
