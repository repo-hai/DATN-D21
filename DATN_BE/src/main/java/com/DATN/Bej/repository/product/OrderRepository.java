package com.DATN.Bej.repository.product;

import com.DATN.Bej.entity.cart.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {

    List<Orders> findAllByUserId(String userId);
    List<Orders> findAllByOrderByOrderAtDesc();
    List<Orders> findDistinctByTypeOrderByOrderAtDesc(int type);

    @Query("""
        SELECT p FROM Orders p
        WHERE LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', :phoneNumber, '%'))
        ORDER BY p.orderAt DESC""")
    List<Orders> searchByPhoneNumberOrderByOrderAtDesc(
            @Param("phoneNumber") String phoneNumber
    );

    /**
     * Lấy tất cả orders trong một tháng cụ thể
     * @param startDate Ngày bắt đầu tháng
     * @param endDate Ngày kết thúc tháng
     * @return Danh sách orders
     */
    List<Orders> findByOrderAtBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Lấy tất cả orders trong một năm
     * @param startDate Ngày bắt đầu năm
     * @param endDate Ngày kết thúc năm
     * @return Danh sách orders
     */
    List<Orders> findByOrderAtBetweenOrderByOrderAtDesc(LocalDate startDate, LocalDate endDate);
    
    /**
     * Tính tổng doanh thu trong khoảng thời gian
     */
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Orders o WHERE o.orderAt BETWEEN :startDate AND :endDate AND o.status IN (2, 5)")
    Double sumTotalPriceByOrderAtBetweenAndStatus(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Đếm số lượng đơn hàng trong khoảng thời gian
     */
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.orderAt BETWEEN :startDate AND :endDate AND o.status IN (2, 5)")
    Long countByOrderAtBetweenAndStatus(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.orderAt BETWEEN :startDate AND :endDate")
    Long countByOrderAtBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.orderAt BETWEEN :startDate AND :endDate AND o.type = :type")
    Long countByOrderAtBetweenAndType(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      @Param("type") int type);
    /**
     * Tính tổng doanh thu trong khoảng thời gian theo type
     */
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Orders o WHERE o.orderAt BETWEEN :startDate AND :endDate AND o.status IN (2, 5) AND o.type = :type")
    Double sumTotalPriceByOrderAtBetweenAndStatusAndType(@Param("startDate") LocalDate startDate, 
                                                          @Param("endDate") LocalDate endDate, 
                                                          @Param("type") int type);
    
    /**
     * Đếm số lượng đơn hàng trong khoảng thời gian theo type
     */
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.orderAt BETWEEN :startDate AND :endDate AND o.status IN (2, 5) AND o.type = :type")
    Long countByOrderAtBetweenAndStatusAndType(@Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate, 
                                                @Param("type") int type);
    
    /**
     * Thống kê dịch vụ sửa chữa được dùng nhiều nhất
     */
    @Query("SELECT o.description, COUNT(o) as usageCount, COALESCE(SUM(o.totalPrice), 0) as totalRevenue " +
           "FROM Orders o " +
           "WHERE o.orderAt BETWEEN :startDate AND :endDate " +
           "AND o.status IN (2, 5) " +
           "AND o.type = 1 " +
           "AND o.description IS NOT NULL " +
           "GROUP BY o.description " +
           "ORDER BY usageCount DESC")
    List<Object[]> findTopRepairServicesByDateRange(@Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);

}
