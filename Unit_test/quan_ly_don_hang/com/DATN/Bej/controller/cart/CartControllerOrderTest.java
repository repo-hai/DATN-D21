package com.DATN.Bej.controller.cart;

import com.DATN.Bej.dto.request.cartRequest.OrderItemRequest;
import com.DATN.Bej.dto.request.cartRequest.OrderRequest;
import com.DATN.Bej.dto.response.cart.OrderDetailsResponse;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.exception.GlobalExceptionHandler;
import com.DATN.Bej.service.guest.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CartControllerOrderTest {

    @Mock
    private CartService cartService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        CartController controller = new CartController(cartService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
        void placeOrder_returnsCreatedOrderDetails() throws Exception {
        // Test Case ID theo report: ITC-CART-CTL-001

        // Arrange: request dat hang hop le va service tra ve don da tao.
        OrderRequest request = OrderRequest.builder()
                .phoneNumber("0912345678")
                .address("12 Nguyễn Trãi")
                .description("Gọi trước khi giao")
                .items(List.of(OrderItemRequest.builder().cartItemId("CART-001").productAttId("ATT-001").quantity(1).build()))
                .build();
        when(cartService.placeOrder(any(OrderRequest.class)))
                .thenReturn(OrderDetailsResponse.builder().id("ORD-CART-01").build());

        // Act & Assert: endpoint tra dung id don hang duoc tao.
        mockMvc.perform(post("/cart/place-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value("ORD-CART-01"));
    }

    @Test
        void getMyOrders_returnsUserOrders() throws Exception {
        // Test Case ID theo report: ITC-CART-CTL-002

        // Arrange: service tra danh sach don hang tu endpoint /cart/my-order.
        when(cartService.getMyOrder()).thenReturn(List.of(
                OrderDetailsResponse.builder().id("ORD-CART-02").build()
        ));

        // Act & Assert: endpoint tra mang result co 1 don hang.
        mockMvc.perform(get("/cart/my-order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(1))
                .andExpect(jsonPath("$.result[0].id").value("ORD-CART-02"));
    }

    @Test
    void placeOrder_serviceThrowsAppException_returnsBadRequest() throws Exception {
        // Test Case ID theo report: ITC-CART-CTL-001-1

        OrderRequest request = OrderRequest.builder()
                .phoneNumber("0912345678")
                .address("12 Nguyễn Trãi")
                .items(List.of())
                .build();
        when(cartService.placeOrder(any(OrderRequest.class)))
                .thenThrow(new AppException(ErrorCode.INVALID_KEY));

        mockMvc.perform(post("/cart/place-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_KEY.getCode()));
    }

    @Test
    void getMyOrders_serviceThrowsAccessDenied_returnsForbidden() throws Exception {
        // Test Case ID theo report: ITC-CART-CTL-002-1

        when(cartService.getMyOrder()).thenThrow(new AccessDeniedException("denied"));

        mockMvc.perform(get("/cart/my-order"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }
}
