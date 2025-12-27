package com.dttlibrary.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Lớp này xử lý các ngoại lệ (exceptions) một cách toàn cục cho tất cả các controller.
 * Thay vì phải viết try-catch ở nhiều nơi, các lỗi sẽ được xử lý tập trung tại đây.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý các lỗi RuntimeException chung, thường là lỗi "Object not found".
     * Nó sẽ lấy URL mà người dùng đang truy cập, chuyển hướng họ trở lại trang trước đó
     * và đính kèm một thông báo lỗi.
     *
     * @param e Exception xảy ra.
     * @param request Request của người dùng.
     * @param redirectAttributes Dùng để gửi thông báo lỗi qua redirect.
     * @return Chuỗi để redirect về trang trước đó.
     */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        // Thêm thông báo lỗi để hiển thị trên giao diện
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

        // Lấy URL của trang trước đó từ header "Referer"
        String referer = request.getHeader("Referer");
        
        // Nếu có trang trước đó, quay lại trang đó. Nếu không, về trang chủ.
        return "redirect:" + (referer != null ? referer : "/");
    }
}
