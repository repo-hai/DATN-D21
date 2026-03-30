package com.DATN.Bej.exception;

import com.DATN.Bej.dto.request.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý tất cả các exception không được handle bởi các handler khác
     * Log chi tiết để debug
     * 
     * LƯU Ý: Handler này chỉ xử lý cho @RestController (trả về JSON)
     * @Controller nên tự xử lý exception trong try-catch để tránh circular view path
     */
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(Exception exception, HttpServletRequest request){
        // Log chi tiết exception để debug
        log.error("❌ Uncategorized exception occurred: {}", exception.getMessage(), exception);
        log.error("   Exception type: {}", exception.getClass().getName());
        log.error("   Request URI: {}", request.getRequestURI());
        log.error("   Request method: {}", request.getMethod());
        if (exception.getCause() != null) {
            log.error("   Caused by: {}", exception.getCause().getMessage());
        }
        
        // Luôn trả về JSON để tránh circular view path
        // @Controller nên tự xử lý exception trong try-catch
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        // Trả về message chi tiết hơn trong development
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage() + 
                " - " + exception.getClass().getSimpleName() + ": " + exception.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
        }
        catch (IllegalArgumentException e){

        }

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception){
        ErrorCode errorCode  = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    /**
     * Xử lý NoResourceFoundException - khi không tìm thấy resource hoặc endpoint
     * Đặc biệt xử lý trường hợp request đến /upload/product-upload (sai URL)
     */
    @ExceptionHandler(value = NoResourceFoundException.class)
    ResponseEntity<ApiResponse> handlingNoResourceFoundException(NoResourceFoundException exception, HttpServletRequest request){
        log.warn("⚠️ Resource not found: {} - Request URI: {}", exception.getMessage(), request.getRequestURI());
        
        // Nếu request đến /upload/product-upload, trả về message hướng dẫn
        if (request.getRequestURI().contains("/upload/product-upload")) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setCode(404);
            apiResponse.setMessage("URL không đúng. Vui lòng sử dụng: /bej3/upload/product");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
        
        // Các trường hợp khác
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(404);
        apiResponse.setMessage("Resource not found: " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

}
