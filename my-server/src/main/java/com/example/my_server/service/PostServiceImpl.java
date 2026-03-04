package com.example.my_server.service;

import com.example.my_server.domain.Post;
import com.example.my_server.domain.User;
import com.example.my_server.exception.PostNotFoundException;
import com.example.my_server.repository.PostRepository;
import com.example.my_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService
{
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // Properties 에서 설정한 경로를 받음
    @Value("${file.upload-dir}")
    private String uploadDir;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository)
    {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<Post> list(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return postRepository.findAll(pageable);
        }

        return postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }

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
    public Long save(Post post, MultipartFile file) throws IOException
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        String username;
        if (principal instanceof UserDetails)
        { username = ((UserDetails) principal).getUsername(); }
        else if (principal instanceof String)
        { username = (String) principal; }
        else
        { throw new RuntimeException("로그인 정보를 찾을 수 없습니다."); }

        User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습ㄴ디ㅏ."));

        post.setUser(user);
        post.setUsername(user.getNickname());


        if (file != null && !file.isEmpty())
        {
            String savedFilePath = saveFile(file);
            post.setFilePath(savedFilePath);
        }

        return postRepository.save(post).getId();
    }

    private String saveFile(MultipartFile file) throws IOException
    {
        // 1. 저장 디렉터리 없을 시 생성
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists())
        { uploadPath.mkdirs(); }

        // 2. 파일명 중복 방지를 위해 UUID 사용
        String originalFilename = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String savedFilename = uuid + "_" + originalFilename;

        // 3. 실제 저장될 전체 경로
        File savedFile = new File(uploadPath.getAbsolutePath(), savedFilename);

        // 4. 파일 저장
        file.transferTo(savedFile);

        // 5. 웹에서 접근 가능한 경로 반환
        return savedFilename;
    }

    // 포스트 수정하기
    @Override
    @Transactional
    public Post updatePost(Long id, Post updatedPost, MultipartFile file) throws IOException
    {
        Post existingPost = findById(id);

        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setUsername(updatedPost.getUsername());
        existingPost.setContent(updatedPost.getContent());

        // 새 파일이 업로드 되었다면 기존 파일 삭제 후 저장
        if (file != null && !file.isEmpty())
        {
            String savedFilePath = saveFile(file);
            existingPost.setFilePath(savedFilePath);
        }

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
