package ec.edu.espe.pedido_service.pedido_service.services;

import ec.edu.espe.pedido_service.pedido_service.dto.OrderRequestDto;
import ec.edu.espe.pedido_service.pedido_service.dto.OrderResponseDto;

import java.util.List;

public interface OrderService {

    OrderResponseDto create(OrderRequestDto request);

    OrderResponseDto findById(Long id);

    List<OrderResponseDto> findAll();

    OrderResponseDto update(Long id, OrderRequestDto request);

    void cancel(Long id);

    List<OrderResponseDto> findByCustomerName(String customerName);



}

