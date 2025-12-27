package com.dttlibrary.controller.user;

import com.dttlibrary.service.BookService;
import com.dttlibrary.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserHomeController {

    private final BookService bookService;
    private final CategoryService categoryService; // Thêm service

    public UserHomeController(BookService bookService, CategoryService categoryService) { // Thêm vào constructor
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @GetMapping("/home")
    public String home(Model model) {

        model.addAttribute("newBooks", bookService.findLatestBooks());
        model.addAttribute("categories", categoryService.findAll()); // Lấy tất cả thể loại

        return "user/home";
    }
}
