package com.DATN.Bej.repository.product;

import com.DATN.Bej.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository <Product, String> {
    boolean existsByName(String name);
    List<Product> findAllByOrderByCreateDateDesc();
    List<Product> findByStatusOrderByCreateDateDesc(int status);

    Optional<Product> findByName(String productName);
    
    /**
     * Tìm products theo category ID
     */
    List<Product> findByCategory_IdAndStatusOrderByCreateDateDesc(Long categoryId, int status);
    
    /**
     * Tìm products theo category ID (tất cả status)
     */
    List<Product> findByCategory_IdOrderByCreateDateDesc(Long categoryId);
    
    /**
     * Tìm products theo tên (gần đúng - case insensitive)
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.status = :status ORDER BY p.createDate DESC")
    List<Product> findByNameContainingIgnoreCaseAndStatus(@Param("name") String name, @Param("status") int status);
    
    /**
     * Tìm products theo tên (gần đúng - tất cả status)
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY p.createDate DESC")
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Tìm products theo category và tên (gần đúng)
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
           "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND p.status = :status ORDER BY p.createDate DESC")
    List<Product> findByCategoryAndNameContainingIgnoreCaseAndStatus(
        @Param("categoryId") Long categoryId, 
        @Param("name") String name, 
        @Param("status") int status
    );
    
    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.variants v " +
           "LEFT JOIN FETCH v.attributes " +
           "LEFT JOIN FETCH v.detailImages " +
           "LEFT JOIN FETCH p.introImages " +
           "WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") String id);
    
    /**
     * Lấy products có phân trang (chỉ status = 1)
     */
    Page<Product> findByStatusOrderByCreateDateDesc(int status, Pageable pageable);
    
    /**
     * Lấy products theo category có phân trang (chỉ status = 1)
     */
    Page<Product> findByCategory_IdAndStatusOrderByCreateDateDesc(Long categoryId, int status, Pageable pageable);
    
    /**
     * Tìm products theo tên có phân trang (chỉ status = 1)
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.status = :status ORDER BY p.createDate DESC")
    Page<Product> findByNameContainingIgnoreCaseAndStatus(@Param("name") String name, @Param("status") int status, Pageable pageable);
    
    /**
     * Tìm products theo category và tên có phân trang (chỉ status = 1)
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
           "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND p.status = :status ORDER BY p.createDate DESC")
    Page<Product> findByCategoryAndNameContainingIgnoreCaseAndStatus(
        @Param("categoryId") Long categoryId, 
        @Param("name") String name, 
        @Param("status") int status,
        Pageable pageable
    );
}
