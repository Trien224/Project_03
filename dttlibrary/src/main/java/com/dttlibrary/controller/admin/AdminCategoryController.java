package com.dttlibrary.controller.admin;

import com.dttlibrary.model.Category;
import com.dttlibrary.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/categories/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        if (!model.containsAttribute("category")) {
            model.addAttribute("category", new Category());
        }
        return "admin/categories/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        // Lỗi "not found" sẽ được GlobalExceptionHandler xử lý
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);
        return "admin/categories/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        // Các lỗi validation (tên trùng, slug trùng) sẽ được GlobalExceptionHandler xử lý
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("successMessage", "Category saved successfully.");
        return "redirect:/admin/categories";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        // Lỗi "đang được sử dụng" sẽ được GlobalExceptionHandler xử lý
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully.");
        return "redirect:/admin/categories";
    }
}
