package com.example.my_server.service;

import com.example.my_server.domain.Post;
import com.example.my_server.domain.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PostService
{
    Long save(Post post, MultipartFile file) throws IOException;
    Page<Post> list(String keyword, PostType type, Pageable pageable);
    Post findById(Long id);
    Post updatePost(Long id, Post updatedPost, MultipartFile file) throws IOException;
    void delete(Long id);
}
