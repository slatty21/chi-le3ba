package com.weekly_bump.Controller;

import com.weekly_bump.Service.UserService;
import com.weekly_bump.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";  // Show login page
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, Model model) {
        User user = userService.getUserByEmail(email).orElse(null);
        if (user != null && user.getPassword().equals(password)) {
            if ("ROLE_ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/home";
            }
        } else {
            model.addAttribute("error", "Invalid credentials.");
            return "login";  // Return to login page if credentials are incorrect
        }
    }

    // Register page
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";  // Show registration page
    }

    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already in use.");
            return "register";  // Return to register page if email is taken
        }
        user.setRole("ROLE_USER");  // Default role is user
        userService.saveUser(user);  // Save new user
        return "redirect:/login";  // Redirect to login page after successful registration
    }
}
