package com.dttlibrary.controller;

import com.dttlibrary.service.BorrowingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/borrowings")
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PostMapping("/borrow")
    public String borrow(@RequestParam Integer bookItemId,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        
        borrowingService.borrowBook(bookItemId, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Book borrowed successfully!");
        return "redirect:/user/borrowings";
    }

    @GetMapping("/return/{id}")
    public String returnBook(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        borrowingService.returnBook(id);
        redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully!");
        return "redirect:/user/borrowings";
    }
}
