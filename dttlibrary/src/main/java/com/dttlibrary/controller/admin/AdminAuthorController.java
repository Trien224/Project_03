package com.dttlibrary.controller.admin;

import com.dttlibrary.model.Author;
import com.dttlibrary.service.AuthorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/authors")
public class AdminAuthorController {

    private final AuthorService authorService;

    public AdminAuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("authors", authorService.findAll());
        return "admin/authors/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("author", new Author());
        return "admin/authors/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Author author, RedirectAttributes redirectAttributes) {
        authorService.save(author);
        redirectAttributes.addFlashAttribute("successMessage", "Author saved successfully.");
        return "redirect:/admin/authors";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("author", authorService.findById(id));
        return "admin/authors/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        authorService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Author deleted successfully.");
        return "redirect:/admin/authors";
    }
}
