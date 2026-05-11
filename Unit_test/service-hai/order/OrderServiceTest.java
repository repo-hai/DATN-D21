package com.DATN.Bej.service.order;

import com.DATN.Bej.dto.request.cartRequest.CreateOrderRequest;
import com.DATN.Bej.dto.request.cartRequest.OrderItemRequest;
import com.DATN.Bej.dto.request.cartRequest.OrderItemsUpdateRequest;
import com.DATN.Bej.dto.request.order.UpdateOrderStatusRequest;
import com.DATN.Bej.dto.response.OrderStatisticsResponse;
import com.DATN.Bej.dto.response.RevenueStatisticsResponse;
import com.DATN.Bej.dto.response.TopProductResponse;
import com.DATN.Bej.dto.response.TopRepairServiceResponse;
import com.DATN.Bej.dto.response.UserResponse;
import com.DATN.Bej.dto.response.WeeklyRevenueResponse;
import com.DATN.Bej.dto.response.cart.OrderDetailsResponse;
import com.DATN.Bej.dto.response.order.OrderStatusUpdateResponse;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.mapper.UserMapper;
import com.DATN.Bej.repository.UserRepository;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
public class OrderServiceTest {
    
    @Autowired
    OrderService orderService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapper userMapper;

    /**
     * Test of getRevenueStatistics method, of class OrderService.
     * Test_case_ID: STATISTIC_001
     */
    @Test
    @Rollback(true)
    public void testGetRevenueStatistics_test1() {
        int year = 2026;
        Integer month = 0;
        
        RevenueStatisticsResponse result = null;
        try{
            result = orderService.getRevenueStatistics(year, month);
        } catch (AppException e){
            // Neu tra về lỗi thì đúng
            assertNull(null);
        } finally {
            // Nếu không trả về lỗi thì sai
            assertNull(1); 
        }
    }
    
    /**
     * Test of getRevenueStatistics method, of class OrderService.
     * Test_case_ID: STATISTIC_002
     */
    @Test
    @Rollback(true)
    public void testGetRevenueStatistics_test2() {
        int year = 2026;
        Integer month = 13;
        
        RevenueStatisticsResponse result = null;
        try{
            result = orderService.getRevenueStatistics(year, month);
        } catch (AppException e){
            // Nếu trả về lỗi thì đúng
            assertNull(null);
        } finally {
            // Nếu không trả về lỗi thì sai
            assertNull(1); 
        }
    }
    
    /**
     * Test of getRevenueStatistics method, of class OrderService.
     * Test_case_ID: STATISTIC_003
     */
    @Test
    @Rollback(true)
    public void testGetRevenueStatistics_test3() {
        int year = 2026;
        Integer month = null;
        
        RevenueStatisticsResponse result = orderService.getRevenueStatistics(year, month);
        
        assertNotNull(result.getMonthlyRevenues().size());
        assertEquals(result.getRepairOrder(), 0);
        assertEquals(result.getSaleOrder(), 0);
        assertEquals(result.getTotalOrders(), 0);
        assertEquals(result.getTotalRevenue(), 0);
    }
    
    /**
     * Test of getRevenueStatistics method, of class OrderService.
     * Test_case_ID: STATISTIC_004
     */
    @Test
    @Rollback(true)
    public void testGetRevenueStatistics_test4() {
        System.out.println("getRevenueStatistics");
        int year = 2026;
        Integer month = 5;
        // Them moi dich vu 1 don hang, trang thai chua hoan thanh
        
        OrderService instance = null;
        RevenueStatisticsResponse expResult = null;
        RevenueStatisticsResponse result = instance.getRevenueStatistics(year, month);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getRevenueStatistics method, of class OrderService.
     * Test_case_ID: STATISTIC_005
     */
    @Test
    @Rollback(true)
    public void testGetRevenueStatistics_test5() {
        System.out.println("getRevenueStatistics");
        int year = 2026;
        Integer month = 5;
        // Them moi dich vu 1 don hang, don dien thoai va sua chua hoan thanh, cac don con lai chua hoan thanh
        
        OrderService instance = null;
        RevenueStatisticsResponse expResult = null;
        RevenueStatisticsResponse result = instance.getRevenueStatistics(year, month);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getRevenueStatistics method, of class OrderService.
     * Test_case_ID: STATISTIC_006
     */
    @Test
    @Rollback(true)
    public void testGetRevenueStatistics_test6() {
        System.out.println("getRevenueStatistics");
        int year = 2026;
        Integer month = 5;
        // Them moi dich vu 1 don hang, trang thai hoan thanh
        
        OrderService instance = null;
        RevenueStatisticsResponse expResult = null;
        RevenueStatisticsResponse result = instance.getRevenueStatistics(year, month);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getRevenueStatistics method, of class OrderService.
     * Test_case_ID: STATISTIC_007
     */
    @Test
    @Rollback(true)
    public void testGetRevenueStatistics_test7() {
        int year = 2026;
        Integer month = 5;
        
        RevenueStatisticsResponse result = orderService.getRevenueStatistics(year, month);
        
        assertNotNull(result.getMonthlyRevenues());
        assertEquals(result.getRepairOrder(), 0);
        assertEquals(result.getSaleOrder(), 0);
        assertEquals(result.getTotalOrders(), 0);
        assertEquals(result.getTotalRevenue(), 0);
    }
    
    /**
     * Test of getOrderStatistics method, of class OrderService.
     * Test_case_ID: STATISTIC_008
     */
    @Test
    @Rollback(true)
    public void testGetOrderStatistics_test8() {
        Integer year = 2026;
        Integer month = 5;
        Integer week = null;
        
        OrderStatisticsResponse result = orderService.getOrderStatistics(year, month, week);

        assertNotNull(result.getTotalOrders());
        assertEquals(result.getTotalPurchaseOrders(), 0);
        assertEquals(result.getTotalRepairOrders(), 0);
    }
    
    /**
     * Test of getOrderStatistics method, of class OrderService.
     * Test_case_ID: STATISTIC_009
     */
    @Test
    @Rollback(true)
    public void testGetOrderStatistics_test9() {
        System.out.println("getOrderStatistics");
        Integer year = 2026;
        Integer month = null;
        Integer week = null;
        
        OrderStatisticsResponse result = orderService.getOrderStatistics(year, month, week);

        assertNotNull(result.getTotalOrders());
        assertEquals(result.getTotalPurchaseOrders(), 0);
        assertEquals(result.getTotalRepairOrders(), 0);
    }
    
    /**
     * Test of getOrderStatistics method, of class OrderService.
     * Test_case_ID: STATISTIC_010
     */
    @Test
    @Rollback(true)
    public void testGetOrderStatistics_test10() {
        System.out.println("getOrderStatistics");
        Integer year = null;
        Integer month = 5;
        Integer week = 3;
        
        OrderStatisticsResponse result = orderService.getOrderStatistics(year, month, week);

        assertNotNull(result.getTotalOrders());
        assertEquals(result.getTotalPurchaseOrders(), 0);
        assertEquals(result.getTotalRepairOrders(), 0);
    }
    
    /**
     * Test of getOrderStatistics method, of class OrderService.
     * Test_case_ID: STATISTIC_011
     */
    @Test
    @Rollback(true)
    public void testGetOrderStatistics_test11() {
        Integer year = 2025;
        Integer month = 1;
        Integer week = 1;
        
        OrderStatisticsResponse result = orderService.getOrderStatistics(year, month, week);

        assertNotNull(result.getTotalOrders());
        assertEquals(result.getTotalPurchaseOrders(), 0);
        assertEquals(result.getTotalRepairOrders(), 0);
    }
    
    /**
     * Test of getOrderStatistics method, of class OrderService.
     * Test_case_ID: STATISTIC_012
     */
    @Test
    @Rollback(true)
    public void testGetOrderStatistics_test12() {
        System.out.println("getOrderStatistics");
        Integer year = 2026;
        Integer month = 15;
        Integer week = 6;
        // Them 1 giao dich mua dien thoai, trang thai hoan thanh
        
        OrderService instance = null;
        OrderStatisticsResponse expResult = null;
        OrderStatisticsResponse result = instance.getOrderStatistics(year, month, week);
        assertEquals(expResult, result);
    }

    /**
     * Test of getTopProducts method, of class OrderService.
     * Test_case_ID: STATISTIC_013
     */
    @Test
    @Rollback(true)
    public void testGetTopProducts_test13() {
        Integer year = 2025;
        Integer month = 1;
        Integer limit = null;
        
        TopProductResponse result = orderService.getTopProducts(year, month, limit);

        assertNotNull(result.getProducts().size());
        assertNotNull(result.getLimit());
    }
    
    /**
     * Test of getTopProducts method, of class OrderService.
     * Test_case_ID: STATISTIC_014
     */
    @Test
    @Rollback(true)
    public void testGetTopProducts_test14() {
        Integer year = 2025;
        Integer month = 1;
        Integer limit = 10;
        
        TopProductResponse result = orderService.getTopProducts(year, month, limit);

        assertNotNull(result.getProducts().size());
        assertNotNull(result.getLimit());
    }
    
    /**
     * Test of getTopProducts method, of class OrderService.
     * Test_case_ID: STATISTIC_015
     */
    @Test
    @Rollback(true)
    public void testGetTopProducts_test15() {
        System.out.println("getTopProducts");
        Integer year = 2025;
        Integer month = null;
        Integer limit = 10;
        // Them 1 giao dich mua dien thoai, trang thai hoan thanh
        
        OrderService instance = null;
        TopProductResponse expResult = null;
        TopProductResponse result = instance.getTopProducts(year, month, limit);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getTopProducts method, of class OrderService.
     * Test_case_ID: STATISTIC_016
     */
    @Test
    @Rollback(true)
    public void testGetTopProducts_test16() {
        Integer year = null;
        Integer month = null;
        Integer limit = 10;
        
        TopProductResponse result = orderService.getTopProducts(year, month, limit);

        assertNotNull(result.getProducts().size());
        assertNotNull(result.getLimit());
    }

    /**
     * Test of getTopRepairServices method, of class OrderService.
     * Test_case_ID: STATISTIC_017
     */
    @Test
    @Rollback(true)
    public void testGetTopRepairServices_test17() {
        Integer year = 2025;
        Integer month = 1;
        Integer limit = null;
        
        TopRepairServiceResponse result = orderService.getTopRepairServices(year, month, limit);
        
        assertNotNull(result.getServices().size());
        assertNotNull(result.getLimit());
    }
    
    /**
     * Test of getTopRepairServices method, of class OrderService.
     * Test_case_ID: STATISTIC_018
     */
    @Test
    @Rollback(true)
    public void testGetTopRepairServices_test18() {
        Integer year = 2025;
        Integer month = 1;
        Integer limit = 10;
        
        TopRepairServiceResponse result = orderService.getTopRepairServices(year, month, limit);
        
        assertNotNull(result.getServices().size());
        assertNotNull(result.getLimit());
    }
    
    /**
     * Test of getTopRepairServices method, of class OrderService.
     * Test_case_ID: STATISTIC_019
     */
    @Test
    @Rollback(true)
    public void testGetTopRepairServices_test19() {
        System.out.println("getTopRepairServices");
        Integer year = 2025;
        Integer month = null;
        Integer limit = 10;
        
        // Them 1 giao dich sua chua, trang thai hoan thanh
        
        OrderService instance = null;
        TopRepairServiceResponse expResult = null;
        TopRepairServiceResponse result = instance.getTopRepairServices(year, month, limit);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getTopRepairServices method, of class OrderService.
     * Test_case_ID: STATISTIC_020
     */
    @Test
    @Rollback(true)
    public void testGetTopRepairServices_test20() {
        Integer year = null;
        Integer month = null;
        Integer limit = 10;
        
        TopRepairServiceResponse result = orderService.getTopRepairServices(year, month, limit);

        assertNotNull(result.getServices().size());
        assertNotNull(result.getLimit());
    }
    
}
