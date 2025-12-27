package com.dttlibrary.controller.admin;

import com.dttlibrary.model.User;
import com.dttlibrary.service.RoleService;
import com.dttlibrary.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminUserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.findAll());
        return "admin/users/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("allRoles", roleService.findAll());
        return "admin/users/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute User user,
                       @RequestParam(required = false) List<Integer> roleIds,
                       RedirectAttributes redirectAttributes) {
        
        userService.saveAdmin(user, roleIds);
        redirectAttributes.addFlashAttribute("successMessage", "User saved successfully.");
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        userService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        return "redirect:/admin/users";
    }
}
