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
@RequestMapping("/posts")
public class  PostController {

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    // List all posts (Accessible by everyone)
    @GetMapping
    public String listPosts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "post-list"; // A page that lists all posts
    }

    // View a single post (Accessible by everyone)
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id).orElse(null);
        if (post == null) {
            model.addAttribute("error", "Post not found.");
            return "post-list";
        }
        model.addAttribute("post", post);
        return "post-view"; // View single post page
    }

    // Create new post (Only accessible by Admin)
    @GetMapping("/create")
    public String createPostPage() {
        return "create-post"; // A page with a form to create a new post
    }

    @PostMapping("/create")
    public String createPost(@RequestParam String title, @RequestParam String content,
                             @RequestParam Long userId, @RequestParam(required = false) MultipartFile postImg,
                             Model model) {
        if (!userService.isAdmin(userId)) {
            model.addAttribute("error", "You must be an admin to create posts.");
            return "create-post"; // If the user is not an admin, return to the create page
        }

        try {
            User user = userService.getUserById(userId).orElse(null);
            if (user != null && userService.isAdmin(user.getId())) {
                Post post = new Post();
                post.setTitle(title);
                post.setContent(content);
                post.setUser(user);
                post.setCreatedDate(LocalDateTime.now());

                if (postImg != null && !postImg.isEmpty()) {
                    post.setPostImg(postImg.getBytes()); // Store the image as byte[]
                }

                postService.savePost(post);
                return "redirect:/posts"; // Redirect to post list page
            } else {
                model.addAttribute("error", "User not found or not an admin.");
                return "create-post"; // If the user is not an admin
            }
        } catch (IOException e) {
            model.addAttribute("error", "Error uploading the image.");
            return "create-post";
        }
    }

    // Edit an existing post (Only accessible by Admin)
    @GetMapping("/edit/{id}")
    public String editPostPage(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id).orElse(null);
        if (post == null) {
            model.addAttribute("error", "Post not found.");
            return "post-list";
        }
        model.addAttribute("post", post);
        return "edit-post"; // Edit post page
    }

    @PostMapping("/edit/{id}")
    public String editPost(@PathVariable Long id, @RequestParam String title, @RequestParam String content,
                           @RequestParam(required = false) MultipartFile postImg, Model model) {
        Post post = postService.getPostById(id).orElse(null);
        if (post == null) {
            model.addAttribute("error", "Post not found.");
            return "post-list";
        }

        post.setTitle(title);
        post.setContent(content);

        try {
            if (postImg != null && !postImg.isEmpty()) {
                post.setPostImg(postImg.getBytes()); // Update image if present
            }
            postService.savePost(post);
            return "redirect:/posts"; // Redirect to post list page after editing
        } catch (IOException e) {
            model.addAttribute("error", "Error uploading the image.");
            return "edit-post"; // Return to the edit page in case of error
        }
    }

    // Delete a post (Only accessible by Admin)
    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id).orElse(null);
        if (post != null) {
            postService.deletePost(id); // Delete the post from the database
        }
        return "redirect:/posts"; // Redirect to the list of posts
    }
}
