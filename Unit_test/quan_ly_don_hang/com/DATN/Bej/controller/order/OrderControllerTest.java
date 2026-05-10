package com.DATN.Bej.controller.order;

import com.DATN.Bej.dto.response.cart.OrderDetailsResponse;
import com.DATN.Bej.dto.response.cart.OrdersResponse;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.exception.GlobalExceptionHandler;
import com.DATN.Bej.service.guest.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private CartService cartService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        OrderController controller = new OrderController(cartService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getMyOrders_serviceReturnsOrders_expectedApiResponseContainsOrders() throws Exception {
        when(cartService.getMyOrder()).thenReturn(List.of(
                OrderDetailsResponse.builder().id("ORD-U-01").build(),
                OrderDetailsResponse.builder().id("ORD-U-02").build()
        ));

        mockMvc.perform(get("/orders/my-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(2));
    }

    @Test
    void getOrderDetails_validId_expectedApiResponseReturned() throws Exception {
        when(cartService.getMyOrderDetails("ORD-U-DETAIL-01"))
                .thenReturn(OrderDetailsResponse.builder().id("ORD-U-DETAIL-01").build());

        mockMvc.perform(get("/orders/ORD-U-DETAIL-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value("ORD-U-DETAIL-01"));
    }

    @Test
    void confirmRepairOrder_validId_expectedOrdersResponseReturned() throws Exception {
        when(cartService.confirmRepairOrder("ORD-REP-01"))
                .thenReturn(OrdersResponse.builder().id("ORD-REP-01").status(2).build());

        mockMvc.perform(put("/orders/repair-order/ORD-REP-01/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value("ORD-REP-01"))
                .andExpect(jsonPath("$.result.status").value(2));
    }

    @Test
    void getMyOrders_serviceThrowsAccessDenied_returnsForbidden() throws Exception {
        when(cartService.getMyOrder()).thenThrow(new AccessDeniedException("denied"));

        mockMvc.perform(get("/orders/my-orders"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }

    @Test
    void getOrderDetails_serviceThrowsAppException_returnsNotFound() throws Exception {
        when(cartService.getMyOrderDetails("ORD-U-404"))
                .thenThrow(new AppException(ErrorCode.USER_NOT_EXISTED));

        mockMvc.perform(get("/orders/ORD-U-404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_EXISTED.getCode()));
    }

    @Test
    void confirmRepairOrder_serviceThrowsAppException_returnsNotFound() throws Exception {
        when(cartService.confirmRepairOrder("ORD-REP-404"))
                .thenThrow(new AppException(ErrorCode.USER_NOT_EXISTED));

        mockMvc.perform(put("/orders/repair-order/ORD-REP-404/confirm"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_EXISTED.getCode()));
    }
}
