package ec.edu.espe.pedido_service.pedido_service.dto;

import ec.edu.espe.pedido_service.pedido_service.model.DeliveryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderRequestDto {

    @NotBlank(message = "Customer name is required")
    @Size(min = 3, max = 100, message = "Customer name must be between 3 and 100 characters")
    private String customerName;

    @NotBlank(message = "Origin is required")
    @Size(min = 3, max = 150, message = "Origin must be between 3 and 150 characters")
    private String origin;

    @NotBlank(message = "Destination is required")
    @Size(min = 3, max = 150, message = "Destination must be between 3 and 150 characters")
    private String destination;

    @NotNull(message = "Delivery type is required")
    private DeliveryType deliveryType;
}