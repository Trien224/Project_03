package com.dttlibrary.controller.user;

import com.dttlibrary.model.Book;
import com.dttlibrary.model.BookImage;
import com.dttlibrary.model.BookItem;
import com.dttlibrary.service.BookItemService;
import com.dttlibrary.service.BookService;
import com.dttlibrary.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user/books")
public class UserBookController {

    private final BookService bookService;
    private final BookItemService bookItemService;
    private final CategoryService categoryService;

    public UserBookController(BookService bookService,
                              BookItemService bookItemService,
                              CategoryService categoryService) {
        this.bookService = bookService;
        this.bookItemService = bookItemService;
        this.categoryService = categoryService;
    }

    // 📚 Danh sách tất cả sách
    @GetMapping
    public String list(Model model) {
        List<Book> books = bookService.findAllWithAvailableItems();
        model.addAttribute("books", books);
        model.addAttribute("pageTitle", "Tất cả sách");
        return "user/books/list";
    }

    // 📚 Danh sách sách theo thể loại
    @GetMapping("/category/{slug}")
    public String listByCategory(@PathVariable String slug, Model model) {
        List<Book> books = bookService.findByCategorySlug(slug);
        model.addAttribute("books", books);
        model.addAttribute("pageTitle", "Sách thể loại: " + slug); // Cần cải thiện để lấy tên đầy đủ
        return "user/books/list";
    }

    // 📖 Chi tiết sách
    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Book book = bookService.findById(id);
        if (book == null) {
            return "redirect:/user/books";
        }
        long available = bookItemService.countAvailableByBookId(id);
        BookItem availableItem = bookItemService.findFirstAvailable(id);
        BookImage primaryImage = bookService.getPrimaryImage(id);
        List<BookImage> images = bookService.getImages(id);

        model.addAttribute("book", book);
        model.addAttribute("available", available);
        model.addAttribute("availableItem", availableItem);
        model.addAttribute("primaryImage", primaryImage);
        model.addAttribute("images", images);

        return "user/books/detail";
    }
}
