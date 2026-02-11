package com.example.my_server.service;

import com.example.my_server.domain.Post;
import com.example.my_server.exception.PostNotFoundException;
import com.example.my_server.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService
{
    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository)
    { this.postRepository = postRepository; }

    @Override
    public Page<Post> list(Pageable pageable)
    { return postRepository.findAll(pageable); }

    // ID로 포스트 찾기
    @Override
    public Post findById(Long id)
    {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("해당 포스트를 찾을 수 없습니다."));
    }

    // 포스트 저장하기
    @Override
    @Transactional
    public Long save(Post post)
    {
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    // 포스트 수정하기
    @Override
    @Transactional // 트랜잭션의 원자성 유지
    public Post updatePost(Long id, Post updatedPost)
    {
        Post existingPost = findById(id);

        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setUsername(updatedPost.getUsername());
        existingPost.setContent(updatedPost.getContent());

        return existingPost;
    }

    // 포스트 삭제하기
    @Override
    @Transactional
    public void delete(Long id)
    {
        postRepository.deleteById(id);
    }
}
