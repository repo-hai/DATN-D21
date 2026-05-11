package com.DATN.Bej.repository.product;

import com.DATN.Bej.entity.cart.Orders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test cho {@link OrderRepository}.
 * CheckDB: co, vi test ghi du lieu that vao H2 in-memory roi doc lai bang query repository.
 * Rollback: co, vi {@link DataJpaTest} chay moi test trong transaction va rollback tu dong sau khi ket thuc.
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class OrderRepositoryDataJpaTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void sumTotalPriceByOrderAtBetweenAndStatus_mixedStatuses_expectedOnlyPaidAndCompletedIncluded() {
        // Test Case ID theo report: ITC-ORD-REP-001

        // Arrange: luu 4 don hang that vao DB test, chi 2 don co status hop le de tinh doanh thu.
        orderRepository.saveAll(List.of(
                buildOrder("0909000001", LocalDate.of(2026, 4, 5), 2, 0, 1_500_000D),
                buildOrder("0909000002", LocalDate.of(2026, 4, 10), 5, 1, 2_000_000D),
                buildOrder("0909000003", LocalDate.of(2026, 4, 12), 0, 0, 3_000_000D),
                buildOrder("0909000004", LocalDate.of(2026, 5, 1), 2, 0, 4_000_000D)
        ));

        // Act: query doanh thu trong thang 4/2026.
        Double totalRevenue = orderRepository.sumTotalPriceByOrderAtBetweenAndStatus(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)
        );

        // Assert: chi tinh 2 don status 2 va 5 trong khoang ngay chi dinh.
        assertThat(totalRevenue).isEqualTo(3_500_000D);
    }

    @Test
    void countByOrderAtBetweenAndType_mixedTypes_expectedCorrectTypeCountReturned() {
        // Test Case ID theo report: ITC-ORD-REP-002

        // Arrange: luu du lieu that de kiem tra truy van dem so don theo type.
        orderRepository.saveAll(List.of(
                buildOrder("0909111111", LocalDate.of(2026, 4, 3), 2, 0, 500_000D),
                buildOrder("0909222222", LocalDate.of(2026, 4, 8), 1, 1, 700_000D),
                buildOrder("0909333333", LocalDate.of(2026, 4, 9), 5, 1, 900_000D),
                buildOrder("0909444444", LocalDate.of(2026, 3, 28), 2, 1, 1_100_000D)
        ));

        // Act: dem so don sua chua trong thang 4/2026.
        Long repairOrderCount = orderRepository.countByOrderAtBetweenAndType(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30),
                1
        );

        // Assert: DB tra dung 2 don type = 1 trong khoang thoi gian can kiem tra.
        assertThat(repairOrderCount).isEqualTo(2L);
    }

    @Test
    void findByOrderAtBetweenOrderByOrderAtDesc_ordersExist_expectedSortedDescending() {
        // Test Case ID theo report: ITC-ORD-REP-003

        // Arrange: luu du lieu that de kiem tra thu tu sap xep khi doc tu DB.
        orderRepository.saveAll(List.of(
                buildOrder("0909555555", LocalDate.of(2026, 4, 2), 2, 0, 600_000D),
                buildOrder("0909666666", LocalDate.of(2026, 4, 20), 2, 0, 800_000D),
                buildOrder("0909777777", LocalDate.of(2026, 4, 11), 2, 1, 900_000D)
        ));

        // Act: doc danh sach don hang trong thang 4/2026 theo sort giam dan.
        List<Orders> orders = orderRepository.findByOrderAtBetweenOrderByOrderAtDesc(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)
        );

        // Assert: xac nhan DB tra dung thu tu ngay dat don tu moi nhat den cu nhat.
        assertThat(orders).hasSize(3);
        assertThat(orders)
                .extracting(Orders::getOrderAt)
                .containsExactly(
                        LocalDate.of(2026, 4, 20),
                        LocalDate.of(2026, 4, 11),
                        LocalDate.of(2026, 4, 2)
                );
    }

    @Test
    void sumTotalPriceByOrderAtBetweenAndStatus_mixedStatusesAndDateRanges_expectedOnlyPaidAndCompletedSummed() {
        // Test Case ID theo report: ITC-ORD-REP-004
        // Aggregate doanh thu của đơn đã thanh toán (status 2 hoặc 5)

        // Arrange: luu 5 don hang voi status va gia khac nhau
        orderRepository.saveAll(List.of(
                buildOrder("0909888888", LocalDate.of(2026, 4, 5), 2, 0, 1_000_000D),   // status 2 -> included
                buildOrder("0909888889", LocalDate.of(2026, 4, 10), 5, 1, 2_000_000D),  // status 5 -> included
                buildOrder("0909888890", LocalDate.of(2026, 4, 12), 1, 0, 3_000_000D),  // status 1 -> excluded
                buildOrder("0909888891", LocalDate.of(2026, 4, 15), 0, 1, 1_500_000D),  // status 0 -> excluded
                buildOrder("0909888892", LocalDate.of(2026, 5, 5), 2, 0, 4_000_000D)    // khac thang -> excluded
        ));

        // Act
        Double totalRevenue = orderRepository.sumTotalPriceByOrderAtBetweenAndStatus(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)
        );

        // Assert: chi tinh tong gia cua don status 2 hoac 5 trong thang 4
        assertThat(totalRevenue).isEqualTo(3_000_000D); // 1_000_000 + 2_000_000
    }

    @Test
    void countByOrderAtBetweenAndStatus_mixedStatuses_expectedCorrectCountOfPaidAndCompletedOrders() {
        // Test Case ID theo report: ITC-ORD-REP-005
        // Đếm số đơn đã thanh toán hoặc hoàn thành trong khoảng thời gian

        // Arrange
        orderRepository.saveAll(List.of(
                buildOrder("0909999991", LocalDate.of(2026, 4, 3), 2, 0, 500_000D),    // status 2 -> counted
                buildOrder("0909999992", LocalDate.of(2026, 4, 8), 5, 1, 700_000D),    // status 5 -> counted
                buildOrder("0909999993", LocalDate.of(2026, 4, 9), 1, 0, 900_000D),    // status 1 -> not counted
                buildOrder("0909999994", LocalDate.of(2026, 4, 25), 0, 1, 1_100_000D), // status 0 -> not counted
                buildOrder("0909999995", LocalDate.of(2026, 3, 28), 2, 1, 800_000D)    // outside range
        ));

        // Act
        Long count = orderRepository.countByOrderAtBetweenAndStatus(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)
        );

        // Assert: 2 don co status 2 hoac 5 trong thang 4
        assertThat(count).isEqualTo(2L);
    }

    @Test
    void countByOrderAtBetween_allOrders_expectedCorrectTotalCountInRange() {
        // Test Case ID theo report: ITC-ORD-REP-006
        // Đếm tổng số đơn trong khoảng ngày (không lọc theo status)

        // Arrange
        orderRepository.saveAll(List.of(
                buildOrder("0909000111", LocalDate.of(2026, 4, 2), 2, 0, 600_000D),
                buildOrder("0909000112", LocalDate.of(2026, 4, 8), 1, 1, 700_000D),
                buildOrder("0909000113", LocalDate.of(2026, 4, 15), 0, 0, 800_000D),
                buildOrder("0909000114", LocalDate.of(2026, 4, 20), 5, 1, 900_000D),
                buildOrder("0909000115", LocalDate.of(2026, 5, 5), 2, 0, 1_000_000D)   // khac thang
        ));

        // Act
        Long totalCount = orderRepository.countByOrderAtBetween(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)
        );

        // Assert: 4 don trong thang 4 (khong phan biet status)
        assertThat(totalCount).isEqualTo(4L);
    }

    @Test
    void countByOrderAtBetweenAndType_allTypesAndStatuses_expectedCountByTypeFilter() {
        // Test Case ID theo report: ITC-ORD-REP-007
        // Đếm số đơn theo loại

        // Arrange
        orderRepository.saveAll(List.of(
                buildOrder("0909000211", LocalDate.of(2026, 4, 3), 2, 0, 500_000D),    // type 0 (mua ban)
                buildOrder("0909000212", LocalDate.of(2026, 4, 8), 1, 1, 700_000D),    // type 1 (sua chua)
                buildOrder("0909000213", LocalDate.of(2026, 4, 9), 5, 1, 900_000D),    // type 1 (sua chua)
                buildOrder("0909000214", LocalDate.of(2026, 4, 25), 0, 0, 1_100_000D), // type 0 (mua ban)
                buildOrder("0909000215", LocalDate.of(2026, 3, 28), 2, 1, 800_000D)    // khac thang
        ));

        // Act: dem so don sua chua trong thang 4
        Long repairCount = orderRepository.countByOrderAtBetweenAndType(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30),
                1
        );

        // Act: dem so don mua ban trong thang 4
        Long purchaseCount = orderRepository.countByOrderAtBetweenAndType(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30),
                0
        );

        // Assert
        assertThat(repairCount).isEqualTo(2L);  // 2 don sua chua
        assertThat(purchaseCount).isEqualTo(2L); // 2 don mua ban
    }

    @Test
    void sumTotalPriceByOrderAtBetweenAndStatusAndType_byType_expectedRevenueFilteredByTypeAndStatus() {
        // Test Case ID theo report: ITC-ORD-REP-008
        // Aggregate doanh thu theo loại đơn (mua bán type=0 hoặc sửa chữa type=1)

        // Arrange
        orderRepository.saveAll(List.of(
                buildOrder("0909000311", LocalDate.of(2026, 4, 5), 2, 0, 1_500_000D),   // type 0 status 2 -> included
                buildOrder("0909000312", LocalDate.of(2026, 4, 10), 5, 0, 2_000_000D),  // type 0 status 5 -> included
                buildOrder("0909000313", LocalDate.of(2026, 4, 12), 1, 1, 3_000_000D),  // type 1 status 1 -> excluded (wrong status)
                buildOrder("0909000314", LocalDate.of(2026, 4, 15), 1, 0, 1_500_000D),  // type 0 status 1 -> excluded (wrong status)
                buildOrder("0909000315", LocalDate.of(2026, 5, 5), 2, 0, 4_000_000D)    // khac thang -> excluded
        ));

        // Act: tinh doanh thu don mua ban (type=0) trong thang 4
        Double purchaseRevenue = orderRepository.sumTotalPriceByOrderAtBetweenAndStatusAndType(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30),
                0
        );

        // Act: tinh doanh thu don sua chua (type=1) trong thang 4
        Double repairRevenue = orderRepository.sumTotalPriceByOrderAtBetweenAndStatusAndType(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30),
                1
        );

        // Assert
        assertThat(purchaseRevenue).isEqualTo(3_500_000D); // 1_500_000 + 2_000_000
        assertThat(repairRevenue).isEqualTo(0D);            // chi co status 2 hoac 5 duoc tinh, khong co don type 1 co status 2 hoac 5
    }

    @Test
    void countByOrderAtBetweenAndStatusAndType_byStatusAndType_expectedCountFilteredCorrectly() {
        // Test Case ID theo report: ITC-ORD-REP-009
        // Đếm số đơn đã thanh toán theo loại

        // Arrange
        orderRepository.saveAll(List.of(
                buildOrder("0909000411", LocalDate.of(2026, 4, 3), 2, 0, 500_000D),    // type 0 status 2 -> counted
                buildOrder("0909000412", LocalDate.of(2026, 4, 8), 5, 1, 700_000D),    // type 1 status 5 -> counted
                buildOrder("0909000413", LocalDate.of(2026, 4, 9), 2, 1, 900_000D),    // type 1 status 2 -> counted
                buildOrder("0909000414", LocalDate.of(2026, 4, 25), 1, 0, 1_100_000D), // type 0 status 1 -> not counted (wrong status)
                buildOrder("0909000415", LocalDate.of(2026, 3, 28), 2, 1, 800_000D)    // outside range
        ));

        // Act: dem don sua chua (type=1) da thanh toan hoac hoan thanh
        Long repairPaidCount = orderRepository.countByOrderAtBetweenAndStatusAndType(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30),
                1
        );

        // Act: dem don mua ban (type=0) da thanh toan hoac hoan thanh
        Long purchasePaidCount = orderRepository.countByOrderAtBetweenAndStatusAndType(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30),
                0
        );

        // Assert
        assertThat(repairPaidCount).isEqualTo(2L);  // 2 don sua chua co status 2 hoac 5
        assertThat(purchasePaidCount).isEqualTo(1L); // 1 don mua ban co status 2 hoac 5
    }

    @Test
    void findTopRepairServicesByDateRange_repairServices_expectedTopServicesGroupedByDescription() {
        // Test Case ID theo report: ITC-ORD-REP-010
        // Query top dịch vụ sửa chữa theo tần suất sử dụng và doanh thu

        // Arrange: luu cac don sua chua voi mo ta dich vu khac nhau
        orderRepository.saveAll(List.of(
                buildOrder("0909000511", LocalDate.of(2026, 4, 5), 2, 1, 500_000D, "Thay man hinh iPhone"),
                buildOrder("0909000512", LocalDate.of(2026, 4, 8), 5, 1, 600_000D, "Thay man hinh iPhone"),
                buildOrder("0909000513", LocalDate.of(2026, 4, 10), 2, 1, 400_000D, "Thay man hinh iPhone"),
                buildOrder("0909000514", LocalDate.of(2026, 4, 12), 2, 1, 800_000D, "Thay pin iPhone"),
                buildOrder("0909000515", LocalDate.of(2026, 4, 15), 5, 1, 700_000D, "Thay pin iPhone"),
                buildOrder("0909000516", LocalDate.of(2026, 4, 20), 1, 1, 300_000D, "Sua chua bo sac"),  // status 1 -> excluded
                buildOrder("0909000517", LocalDate.of(2026, 5, 1), 2, 1, 900_000D, "Thay man hinh iPhone") // khac thang
        ));

        // Act
        List<Object[]> topServices = orderRepository.findTopRepairServicesByDateRange(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)
        );

        // Assert: 2 dich vu (Thay man hinh iPhone - 3 lan, Thay pin iPhone - 2 lan)
        assertThat(topServices).hasSize(2);
        // Top 1: Thay man hinh iPhone (3 lan, doanh thu: 500k + 600k + 400k = 1.5M)
        assertThat(topServices.get(0)[0]).isEqualTo("Thay man hinh iPhone");
        assertThat(topServices.get(0)[1]).isEqualTo(3L);
        assertThat(topServices.get(0)[2]).isEqualTo(1_500_000D);
        // Top 2: Thay pin iPhone (2 lan, doanh thu: 800k + 700k = 1.5M)
        assertThat(topServices.get(1)[0]).isEqualTo("Thay pin iPhone");
        assertThat(topServices.get(1)[1]).isEqualTo(2L);
        assertThat(topServices.get(1)[2]).isEqualTo(1_500_000D);
    }

    private Orders buildOrder(String phoneNumber, LocalDate orderAt, int status, int type, double totalPrice) {
        return buildOrder(phoneNumber, orderAt, status, type, totalPrice, "Đơn test quản lý đơn hàng");
    }

    private Orders buildOrder(String phoneNumber, LocalDate orderAt, int status, int type, double totalPrice, String description) {
        Orders order = new Orders();
        order.setPhoneNumber(phoneNumber);
        order.setEmail(phoneNumber + "@test.local");
        order.setAddress("Hà Nội");
        order.setOrderAt(orderAt);
        order.setUpdatedAt(orderAt);
        order.setDescription(description);
        order.setStatus(status);
        order.setType(type);
        order.setTotalPrice(totalPrice);
        return order;
    }
}


