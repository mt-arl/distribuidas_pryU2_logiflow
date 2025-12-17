package ec.edu.espe.pedido_service.pedido_service.repositories;

import ec.edu.espe.pedido_service.pedido_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByActiveTrue();
    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);
}

