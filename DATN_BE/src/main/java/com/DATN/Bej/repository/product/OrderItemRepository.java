package com.DATN.Bej.repository.product;

import com.DATN.Bej.entity.cart.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    
    @Query("SELECT oi.productA.id, SUM(oi.quantity) as totalSold, SUM(oi.price * oi.quantity) as totalRevenue " +
           "FROM OrderItem oi " +
           "WHERE oi.order.orderAt BETWEEN :startDate AND :endDate " +
//           "AND oi.order.status IN (2, 5) " +
//           "AND oi.order.type = 0 " +
           "GROUP BY oi.productA.id " +
           "ORDER BY totalSold DESC")
    List<Object[]> findTopProductsByDateRange(@Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
}

