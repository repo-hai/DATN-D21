package com.DATN.Bej.repository.product;

import com.DATN.Bej.entity.cart.OrderItem;
import com.DATN.Bej.entity.cart.Orders;
import com.DATN.Bej.entity.product.ProductAttribute;
import com.DATN.Bej.entity.product.ProductVariant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test cho {@link OrderItemRepository}.
 * CheckDB: co, vi test ghi du lieu that vao H2 in-memory roi doc lai bang query repository.
 * Rollback: co, vi {@link DataJpaTest} chay moi test trong transaction va rollback tu dong sau khi ket thuc.
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class OrderItemRepositoryDataJpaTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductAttributeRepository productAttributeRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Test
    void findTopProductsByDateRange_multipleProducts_expectedTopProductsSortedBySalesVolume() {
        // Test Case ID theo report: ITC-ORD-REP-011
        // Query top sản phẩm bán chạy theo tổng số lượng bán và doanh thu

        // Arrange: tao san pham variant va product attribute
        ProductVariant variant1 = new ProductVariant(null, "SKU-iPhone15-BLK", "Black", null, null, null);
        ProductVariant variant2 = new ProductVariant(null, "SKU-iPhone15-WHT", "White", null, null, null);
        ProductVariant variant3 = new ProductVariant(null, "SKU-iPhone14-BLK", "Black", null, null, null);

        productVariantRepository.saveAll(List.of(variant1, variant2, variant3));

        ProductAttribute attr1 = new ProductAttribute(null, "iPhone 15 128GB Black", 500_000, 500_000, 0, 100, 0, 0, variant1);
        ProductAttribute attr2 = new ProductAttribute(null, "iPhone 15 256GB White", 600_000, 600_000, 0, 100, 0, 0, variant2);
        ProductAttribute attr3 = new ProductAttribute(null, "iPhone 14 128GB Black", 400_000, 400_000, 0, 100, 0, 0, variant3);

        productAttributeRepository.saveAll(List.of(attr1, attr2, attr3));

        // Tao cac don hang trong thang 4/2026
        Orders order1 = new Orders(null, null, "0909000601", "0909000601@test.local", "Hà Nội", LocalDate.of(2026, 4, 5), LocalDate.of(2026, 4, 5), "Đơn test", 3_000_000D, null, null, 0, 2);
        Orders order2 = new Orders(null, null, "0909000602", "0909000602@test.local", "Hà Nội", LocalDate.of(2026, 4, 8), LocalDate.of(2026, 4, 8), "Đơn test", 2_500_000D, null, null, 0, 2);
        Orders order3 = new Orders(null, null, "0909000603", "0909000603@test.local", "Hà Nội", LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 10), "Đơn test", 1_500_000D, null, null, 0, 5);
        Orders order4 = new Orders(null, null, "0909000604", "0909000604@test.local", "Hà Nội", LocalDate.of(2026, 5, 5), LocalDate.of(2026, 5, 5), "Đơn test", 2_000_000D, null, null, 0, 2);  // khac thang

        orderRepository.saveAll(List.of(order1, order2, order3, order4));

        // Tao order items:
        // Order1: attr1 x 2 (500k x 2), attr2 x 1 (600k)
        // Order2: attr1 x 1 (500k), attr3 x 1 (400k)
        // Order3: attr1 x 3 (500k x 3)
        // Order4: attr2 x 2 (600k x 2) -> khác tháng, không tính
        OrderItem item1 = new OrderItem(null, order1, attr1, 2, 500_000);
        OrderItem item2 = new OrderItem(null, order1, attr2, 1, 600_000);
        OrderItem item3 = new OrderItem(null, order2, attr1, 1, 500_000);
        OrderItem item4 = new OrderItem(null, order2, attr3, 1, 400_000);
        OrderItem item5 = new OrderItem(null, order3, attr1, 3, 500_000);
        OrderItem item6 = new OrderItem(null, order4, attr2, 2, 600_000); // ngoai thang

        orderItemRepository.saveAll(List.of(item1, item2, item3, item4, item5, item6));

        // Act
        List<Object[]> topProducts = orderItemRepository.findTopProductsByDateRange(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)
        );

        // Assert: 3 san pham trong thang 4 duoc sap xep theo so luong ban (descending)
        assertThat(topProducts).hasSize(3);

        // Verify quantities and revenues are correct (order by quantity desc)
        // Top 1: 6 items, revenue 3M (must be attr1)
        assertThat(topProducts.get(0)[1]).isEqualTo(6L);          // totalSold
        assertThat(topProducts.get(0)[2]).isEqualTo(3_000_000D);   // totalRevenue

        // Top 2: 1 item, revenue 600k (could be attr2)
        assertThat(topProducts.get(1)[1]).isEqualTo(1L);          // totalSold
        assertThat(topProducts.get(1)[2]).isEqualTo(600_000D);     // totalRevenue

        // Top 3: 1 item, revenue 400k (could be attr3)
        assertThat(topProducts.get(2)[1]).isEqualTo(1L);          // totalSold
        assertThat(topProducts.get(2)[2]).isEqualTo(400_000D);     // totalRevenue
    }
}


