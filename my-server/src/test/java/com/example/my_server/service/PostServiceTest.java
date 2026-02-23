package com.example.my_server.service;

import com.example.my_server.domain.Post;
import com.example.my_server.exception.PostNotFoundException;
import com.example.my_server.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException; // 추가
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest
{
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;

    @BeforeEach
    void setUp()
    { post = new Post("테스트 제목", "테스터", "테스트 내용"); }

    @Test
    @DisplayName("게시글 저장 - 성공")
    void savePost_Success() throws IOException // ✅ throws IOException 추가
    {
        // given
        given(postRepository.save(any(Post.class))).willAnswer(invocation -> {
            Post argument = invocation.getArgument(0);
            return argument;
        });

        // when
        // ✅ 두 번째 인자로 null 전달 (파일 업로드 없는 경우 테스트)
        Long savedId = postService.save(post, null);

        // then
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 단건 조회 - 성공")
    void findById_Success()
    {
        // given
        Long postId = 1L;
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        Post foundPost = postService.findById(postId);

        // then
        assertNotNull(foundPost);
        assertEquals("테스트 제목", foundPost.getTitle());
        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("게시글 단건 조회 - 실패 (게시글 없음)")
    void findById_Fail_NotFound()
    {
        // given
        Long postId = 99L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThrows(PostNotFoundException.class, () -> {
            postService.findById(postId);
        });
    }

    @Test
    @DisplayName("게시글 수정 - 성공")
    void updatePost_Success() throws IOException // ✅ throws IOException 추가
    {
        // given
        Long postId = 1L;
        Post existingPost = new Post("기존 제목", "기존 작성자", "기존 내용");
        Post updateData = new Post("수정된 제목", "수정된 작성자", "수정된 내용");

        given(postRepository.findById(postId)).willReturn(Optional.of(existingPost));

        // when
        // ✅ 세 번째 인자로 null 전달 (파일 업로드 없는 경우 테스트)
        Post updatedPost = postService.updatePost(postId, updateData, null);

        // then
        assertEquals("수정된 제목", updatedPost.getTitle());
        assertEquals("수정된 작성자", updatedPost.getUsername());
        assertEquals("수정된 내용", updatedPost.getContent());

        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("게시글 삭제 - 성공")
    void deletePost_Success()
    {
        // given
        Long postId = 1L;

        // when
        postService.delete(postId);

        // then
        verify(postRepository).deleteById(postId);
    }
}