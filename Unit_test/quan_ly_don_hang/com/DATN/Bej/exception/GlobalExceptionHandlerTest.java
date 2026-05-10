package com.DATN.Bej.exception;

import com.DATN.Bej.dto.request.payment.CreatePaymentRequest;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new StubController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleAppException_mapsToNotFoundResponse() throws Exception {
        // Test Case ID theo report: ITC-ORD-EXC-001

        // Arrange/Act/Assert: controller stub nem AppException va handler phai map dung HTTP status + body.
        mockMvc.perform(post("/stub/app-exception"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_EXISTED.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_EXISTED.getMessage()));
    }

    @Test
    void handleMethodArgumentNotValid_returnsBadRequest() throws Exception {
        // Test Case ID theo report: ITC-ORD-EXC-002

        // Arrange/Act/Assert: body thieu orderId khien @Valid fail va handler phai tra HTTP 400.
        mockMvc.perform(post("/stub/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_KEY.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_KEY.getMessage()));
    }

    @RestController
    static class StubController {

        @PostMapping("/stub/app-exception")
        String throwAppException() {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        @PostMapping("/stub/validate")
        String validate(@RequestBody @Valid CreatePaymentRequest request) {
            return request.getOrderId();
        }
    }
}
