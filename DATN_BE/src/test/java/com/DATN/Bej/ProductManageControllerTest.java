package com.DATN.Bej;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.DATN.Bej.controller.manage.ProductManageController;
import com.DATN.Bej.dto.response.productResponse.ProductListResponse;
import com.DATN.Bej.dto.response.productResponse.ProductResponse;
import com.DATN.Bej.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductManageController.class)
class ProductManageControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean ProductService productService;

    // ── Helper ───────────────────────────────────────────────────────────────

    private ProductResponse fakeProductResponse(String id, String name) {
        ProductResponse r = new ProductResponse();
        r.setId(id);
        r.setName(name);
        return r;
    }

    private ProductListResponse fakeListResponse(String name) {
        ProductListResponse r = new ProductListResponse();
        r.setName(name);
        return r;
    }


    // ════════════════════════════════════════════════════════════════════════
    // GET /manage/product/list
    // ════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("GET /manage/product/list")
    class GetAllProductsTest {

        @Test
        @DisplayName("TC_PMC_GAP_01 – ADMIN → 200 + danh sách products")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn200_whenAdmin() throws Exception {
            when(productService.getAllProducts())
                    .thenReturn(List.of(fakeListResponse("iPhone 15")));

            mockMvc.perform(get("/manage/product/list"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result[0].name").value("iPhone 15"));

            verify(productService).getAllProducts();
        }

        @Test
        @DisplayName("TC_PMC_GAP_02 – Không có token → 401 Unauthorized")
        void shouldReturn401_whenUnauthenticated() throws Exception {
            mockMvc.perform(get("/manage/product/list"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(productService);
        }

        @Test
        @DisplayName("TC_PMC_GAP_03– USER thường → 403 Forbidden")
        @WithMockUser(roles = "USER")
        void shouldReturn403_whenNotAdmin() throws Exception {
            mockMvc.perform(get("/manage/product/list"))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(productService);
        }
    }


    // ════════════════════════════════════════════════════════════════════════
    // GET /manage/product/{productId}
    // ════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("GET /manage/product/{productId}")
    class GetProductDetailsTest {

        @Test
        @DisplayName("TC_PMC_GPD_01 – ADMIN + id hợp lệ → 200 + ProductResponse")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn200WithProduct_whenAdminAndValidId() throws Exception {
            when(productService.getProductDetails("p123"))
                    .thenReturn(fakeProductResponse("p123", "Samsung S24"));

            mockMvc.perform(get("/manage/product/p123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value("p123"))
                    .andExpect(jsonPath("$.result.name").value("Samsung S24"));
        }

        @Test
        @DisplayName("TC_PMC_GPD_02 – Không có token → 401")
        void shouldReturn401_whenUnauthenticated() throws Exception {
            mockMvc.perform(get("/manage/product/p123"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("TC_PMC_GPD_03 – USER thường → 403")
        @WithMockUser(roles = "USER")
        void shouldReturn403_whenNotAdmin() throws Exception {
            mockMvc.perform(get("/manage/product/p123"))
                    .andExpect(status().isForbidden());
        }
    }


    // ════════════════════════════════════════════════════════════════════════
    // POST /manage/product/add
    // ════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("POST /manage/product/add")
    class AddNewProductTest {

        @Test
        @DisplayName("TC_PMC_ANP_01 – ADMIN + request hợp lệ → 200 + ProductResponse")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn200_whenAdminAndValidRequest() throws Exception {
            when(productService.addNewProduct(any()))
                    .thenReturn(fakeProductResponse("new1", "Pixel 8"));

            mockMvc.perform(multipart("/manage/product/add")
                            .param("name", "Pixel 8")
                            .param("status", "1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.name").value("Pixel 8"));
        }

        @Test
        @DisplayName("TC_PMC_ANP_02 – Không có token → 401")
        void shouldReturn401_whenUnauthenticated() throws Exception {
            mockMvc.perform(multipart("/manage/product/add")
                            .param("name", "Pixel 8")
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("TC_PMC_ANP_03 – USER thường → 403")
        @WithMockUser(roles = "USER")
        void shouldReturn403_whenNotAdmin() throws Exception {
            mockMvc.perform(multipart("/manage/product/add")
                            .param("name", "Pixel 8")
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }


    // ════════════════════════════════════════════════════════════════════════
    // PUT /manage/product/update/{productId}
    // ════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("PUT /manage/product/update/{productId}")
    class UpdateProductTest {

        @Test
        @DisplayName("TC_PMC_UPD_01 – ADMIN + id hợp lệ → 200 + ProductResponse đã cập nhật")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn200_whenAdminUpdatesProduct() throws Exception {
            when(productService.updateProduct(eq("p123"), any()))
                    .thenReturn(fakeProductResponse("p123", "iPhone 15 Updated"));

            mockMvc.perform(multipart("/manage/product/update/p123")
                            .param("name", "iPhone 15 Updated")
                            .with(req -> { req.setMethod("PUT"); return req; })
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.name").value("iPhone 15 Updated"));
        }

        @Test
        @DisplayName("TC_PMC_UPD_02 – Không có token → 401")
        void shouldReturn401_whenUnauthenticated() throws Exception {
            mockMvc.perform(multipart("/manage/product/update/p123")
                            .param("name", "test")
                            .with(req -> { req.setMethod("PUT"); return req; })
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("TC_PMC_UPD_03 – USER thường → 403")
        @WithMockUser(roles = "USER")
        void shouldReturn403_whenNotAdmin() throws Exception {
            mockMvc.perform(multipart("/manage/product/update/p123")
                            .param("name", "test")
                            .with(req -> { req.setMethod("PUT"); return req; })
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }


    // ════════════════════════════════════════════════════════════════════════
    // DELETE /manage/product/delete/{productId}
    // ════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("DELETE /manage/product/delete/{productId}")
    class DeleteProductTest {

        @Test
        @DisplayName("TC_PMC_DPD_01 – ADMIN → 200, service.delete() được gọi đúng")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn200AndCallDelete_whenAdmin() throws Exception {
            doNothing().when(productService).delete("p123");

            mockMvc.perform(delete("/manage/product/delete/p123")
                            .with(csrf()))
                    .andExpect(status().isOk());

            verify(productService).delete("p123");
        }

        @Test
        @DisplayName("TC_PMC_DPD_02 – Không có token → 401")
        void shouldReturn401_whenUnauthenticated() throws Exception {
            mockMvc.perform(delete("/manage/product/delete/p123")
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(productService);
        }

        @Test
        @DisplayName("TC_PMC_DPD_03 – USER thường → 403")
        @WithMockUser(roles = "USER")
        void shouldReturn403_whenNotAdmin() throws Exception {
            mockMvc.perform(delete("/manage/product/delete/p123")
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }


    // ════════════════════════════════════════════════════════════════════════
    // PUT /manage/product/inactive/{productId}
    // ════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("PUT /manage/product/inactive/{productId}")
    class InactiveProductTest {

        @Test
        @DisplayName("TC_PMC_INA_01 – ADMIN → 200, service.inactive() được gọi đúng")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn200AndCallInactive_whenAdmin() throws Exception {
            doNothing().when(productService).inactive("p123");

            mockMvc.perform(put("/manage/product/inactive/p123")
                            .with(csrf()))
                    .andExpect(status().isOk());

            verify(productService).inactive("p123");
        }

        @Test
        @DisplayName("TC_PMC_INA_02 – Không có token → 401")
        void shouldReturn401_whenUnauthenticated() throws Exception {
            mockMvc.perform(put("/manage/product/inactive/p123")
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(productService);
        }

        @Test
        @DisplayName("TC_PMC_INA_03 – USER thường → 403")
        @WithMockUser(roles = "USER")
        void shouldReturn403_whenNotAdmin() throws Exception {
            mockMvc.perform(put("/manage/product/inactive/p123")
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }
}