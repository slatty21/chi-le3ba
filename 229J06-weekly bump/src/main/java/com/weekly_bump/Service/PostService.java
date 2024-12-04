package com.weekly_bump.Service;

import com.weekly_bump.Model.Post;
import com.weekly_bump.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // Save a new post or update an existing one
    public void savePost(Post post) {
        postRepository.save(post);
    }

    // Get all posts
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // Get latest posts (e.g., for the homepage)
    public List<Post> getLatestPosts() {
        return postRepository.findTop5ByOrderByCreatedDateDesc();
    }

    // Get a single post by its ID
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    // Delete a post by its ID
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
