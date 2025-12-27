package com.dttlibrary.controller.user;

import com.dttlibrary.model.User;
import com.dttlibrary.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/profile")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    private void addUserToModel(UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
    }

    @GetMapping
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addUserToModel(userDetails, model);
        return "user/profile";
    }

    @GetMapping("/edit")
    public String editProfileForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addUserToModel(userDetails, model);
        return "user/edit-profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        userService.updateProfile(user);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        return "redirect:/user/profile";
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "user/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            return "redirect:/user/profile/change-password";
        }
        
        userService.changePassword(userDetails.getUsername(), currentPassword, newPassword);
        redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
        return "redirect:/user/profile";
    }
}
