package com.dttlibrary.controller.admin;

import com.dttlibrary.model.Book;
import com.dttlibrary.model.BookItem;
import com.dttlibrary.service.BookItemService;
import com.dttlibrary.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/book-items")
public class AdminBookItemController {

    private final BookItemService itemService;
    private final BookService bookService;

    public AdminBookItemController(BookItemService itemService, BookService bookService) {
        this.itemService = itemService;
        this.bookService = bookService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", itemService.findAll());
        return "admin/book-items/list";
    }

    @GetMapping("/create")
    public String create(@RequestParam(required = false) Integer bookId, Model model) {
        BookItem item = new BookItem();
        if (bookId != null) {
            Book book = bookService.findById(bookId);
            item.setBook(book);
        }
        model.addAttribute("item", item);
        model.addAttribute("books", bookService.findAll());
        return "admin/book-items/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("item", itemService.findById(id));
        model.addAttribute("books", bookService.findAll());
        return "admin/book-items/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BookItem item,
                       @RequestParam("bookId") Integer bookId,
                       RedirectAttributes redirectAttributes) {

        itemService.saveAdminItem(item, bookId);
        redirectAttributes.addFlashAttribute("successMessage", "Book Item saved successfully.");
        
        // Chuyển hướng về trang sửa sách gốc để người dùng thấy thay đổi
        return "redirect:/admin/books/edit/" + bookId;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, 
                         @RequestParam(required = false) Integer bookId, 
                         RedirectAttributes redirectAttributes) {
        
        itemService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Book Item deleted successfully.");

        // Nếu có bookId, quay lại trang sửa sách. Nếu không, về trang danh sách item.
        if (bookId != null) {
            return "redirect:/admin/books/edit/" + bookId;
        }
        return "redirect:/admin/book-items";
    }
}
