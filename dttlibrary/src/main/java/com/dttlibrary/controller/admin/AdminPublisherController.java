package com.dttlibrary.controller.admin;

import com.dttlibrary.model.Publisher;
import com.dttlibrary.service.PublisherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/publishers")
public class AdminPublisherController {

    private final PublisherService publisherService;

    public AdminPublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("publishers", publisherService.findAll());
        return "admin/publishers/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("publisher", new Publisher());
        return "admin/publishers/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("publisher", publisherService.findById(id));
        return "admin/publishers/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Publisher publisher, RedirectAttributes redirectAttributes) {
        publisherService.save(publisher);
        redirectAttributes.addFlashAttribute("successMessage", "Publisher saved successfully.");
        return "redirect:/admin/publishers";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            publisherService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Publisher deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete publisher. It might be in use.");
        }
        return "redirect:/admin/publishers";
    }
}
