package com.dttlibrary.controller.admin;

import com.dttlibrary.model.Book;
import com.dttlibrary.service.*; // Import all services
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/books")
public class AdminBookController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final PublisherService publisherService;
    private final BookItemService bookItemService; // Service để gọi addCopies

    public AdminBookController(BookService bookService, CategoryService categoryService, 
                               AuthorService authorService, PublisherService publisherService,
                               BookItemService bookItemService) { // Thêm BookItemService vào constructor
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.authorService = authorService;
        this.publisherService = publisherService;
        this.bookItemService = bookItemService; // Gán giá trị
    }

    private void addCategoryAuthorPublisherToModel(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("publishers", publisherService.findAll());
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "admin/books/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("book", new Book());
        addCategoryAuthorPublisherToModel(model);
        return "admin/books/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("book", bookService.findByIdWithItems(id));
        addCategoryAuthorPublisherToModel(model);
        return "admin/books/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Book bookFromForm,
                       @RequestParam Integer categoryId,
                       @RequestParam(required = false) Integer authorId,
                       @RequestParam(required = false) Integer publisherId,
                       @RequestParam(required = false) MultipartFile image,
                       @RequestParam(defaultValue = "0") Integer initialQuantity,
                       RedirectAttributes redirectAttributes) {

        Book savedBook = bookService.saveAdminBook(bookFromForm, categoryId, authorId, publisherId, image, initialQuantity);
        redirectAttributes.addFlashAttribute("successMessage", "Book saved successfully.");
        return "redirect:/admin/books/edit/" + savedBook.getId();
    }

    @PostMapping("/add-copies/{bookId}")
    public String addCopies(@PathVariable Integer bookId,
                            @RequestParam(defaultValue = "1") Integer quantityToAdd,
                            RedirectAttributes redirectAttributes) {
        
        // Gọi phương thức từ đúng service
        bookItemService.addCopies(bookId, quantityToAdd);
        redirectAttributes.addFlashAttribute("successMessage", "Added " + quantityToAdd + " new copies.");
        return "redirect:/admin/books/edit/" + bookId;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        bookService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully.");
        return "redirect:/admin/books";
    }
}
