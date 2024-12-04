package com.weekly_bump.Controller;

import com.weekly_bump.Model.Post;
import com.weekly_bump.Model.User;
import com.weekly_bump.Service.PostService;
import com.weekly_bump.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public AdminController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    // Display admin dashboard for creating posts (only accessible by admin)
    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model, @SessionAttribute(name = "loggedInUser", required = false) User loggedInUser) {
        if (loggedInUser == null || !"ROLE_ADMIN".equals(loggedInUser.getRole())) {
            return "redirect:/home"; // Redirect to home if not an admin
        }
        return "admindashboard"; // Show admin dashboard page
    }

    // Show create post page
    @GetMapping("/create-post")
    public String showCreatePostPage(Model model, @SessionAttribute(name = "loggedInUser", required = false) User loggedInUser) {
        if (loggedInUser == null || !"ROLE_ADMIN".equals(loggedInUser.getRole())) {
            return "redirect:/home"; // Redirect to home if not an admin
        }
        return "create-post"; // Show create post page
    }

    // Handle post creation (only for admins)
    @PostMapping("/create-post")
    public String createPost(@RequestParam String title, @RequestParam String content,
                             @RequestParam MultipartFile postImg, @SessionAttribute(name = "loggedInUser") User loggedInUser, Model model) {
        try {
            if (loggedInUser == null || !"ROLE_ADMIN".equals(loggedInUser.getRole())) {
                model.addAttribute("error", "You must be an admin to create a post.");
                return "redirect:/home"; // Redirect to home if not an admin
            }

            // Create and save the post
            Post post = new Post();
            post.setTitle(title);
            post.setContent(content);
            post.setUser(loggedInUser); // Associate the post with the logged-in user
            post.setCreatedDate(LocalDateTime.now());

            // Handle image upload if available
            if (postImg != null && !postImg.isEmpty()) {
                try {
                    post.setPostImg(postImg.getBytes()); // Save image as byte[] in database
                } catch (IOException e) {
                    model.addAttribute("error", "Error uploading the image. Please try again.");
                    return "create-post";
                }
            }

            postService.savePost(post); // Save the post to the database
            return "redirect:/admin/dashboard";  // Redirect to admin dashboard after saving the post
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred while creating the post. Please try again.");
            return "create-post"; // Return to create post page if error occurs
        }
    }
}
