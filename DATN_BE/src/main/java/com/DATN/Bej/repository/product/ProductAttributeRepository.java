package com.DATN.Bej.repository.product;

import com.DATN.Bej.entity.product.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, String> {

    @Modifying
    @Query("UPDATE ProductAttribute p SET p.soldQuantity = p.soldQuantity + :qty WHERE p.id = :id")
    void increaseSoldQuantity(@Param("id") UUID id, @Param("qty") int qty);

}
