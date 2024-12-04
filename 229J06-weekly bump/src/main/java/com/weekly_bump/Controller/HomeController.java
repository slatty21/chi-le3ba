package com.weekly_bump.Controller;

import com.weekly_bump.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final PostService postService;

    @Autowired
    public HomeController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("posts", postService.getLatestPosts());  // Show latest posts
        return "home";  // Render the home page
    }
}
