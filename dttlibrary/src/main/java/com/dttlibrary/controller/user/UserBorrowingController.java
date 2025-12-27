package com.dttlibrary.controller.user;

import com.dttlibrary.service.BorrowingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserBorrowingController {

    private final BorrowingService borrowingService;

    public UserBorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @GetMapping("/borrowings")
    public String borrowings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("borrowings", borrowingService.findByUsername(userDetails.getUsername()));
        return "user/borrowings";
    }
}
