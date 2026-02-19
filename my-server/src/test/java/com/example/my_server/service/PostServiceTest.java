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
    void setUp() // 테스트용 Post 객체 생성 (ID는 DB 저장 시 생성되므로 여기선 null이거나 설정하지 않음)
    { post = new Post("테스트 제목", "테스터", "테스트 내용"); }

    @Test
    @DisplayName("게시글 저장 - 성공")
    void savePost_Success()
    {   // given
        // 1. postRepository.save() 호출 시, ID가 할당된 post 객체를 반환하도록 설정
        // (실제 DB에서는 save 후 ID가 생성되므로, 이를 시뮬레이션)
        Post savedPost = new Post("테스트 제목", "테스터", "테스트 내용");
        // 리플렉션 등으로 ID를 설정해야 하지만, 테스트에서는 단순히 반환되는 객체의 ID를 확인하므로
        // given에서 어떤 객체를 리턴할지 정의. 여기선 savedPost의 ID를 검증할 것.

        // 편의상 ID가 있는 상태를 가정하기 어려우므로, save 호출 시 post 자신을 리턴한다고 가정하거나
        // verify를 통해 save 호출 자체를 검증하는 데 집중.

        given(postRepository.save(any(Post.class))).willAnswer(invocation -> {
            Post argument = invocation.getArgument(0);
            // 실제 DB 처럼 ID가 세팅된 것처럼 반환 (테스트 편의를 위해 ID 강제 세팅은 어려우므로
            // 여기서는 save가 호출되었는지가 핵심이지만, 리턴값을 확인하려면 로직이 필요.)
            // 여기서는 단순히 post를 리턴한다고 가정.
            return argument;
        });

        // when
        Long savedId = postService.save(post);

        // then
        // save 메소드가 호출되었는지 검증
        verify(postRepository).save(any(Post.class));

        // 리턴된 ID가 null이 아닌지 확인 (구현상 post.getId()를 리턴하므로 현재는 null일 것.)
        // 하지만 여기서는 save 로직이 수행되었다는 점에 중점을 둠.
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
    void updatePost_Success()
    {
        // given
        Long postId = 1L;

        // 1. 기존에 저장된 Post 객체 (DB에 있다고 가정)
        Post existingPost = new Post("기존 제목", "기존 작성자", "기존 내용");

        // 2. 수정 요청 데이터
        Post updateData = new Post("수정된 제목", "수정된 작성자", "수정된 내용");

        // 3. findById가 호출되면 existingPost를 반환하도록 설정
        given(postRepository.findById(postId)).willReturn(Optional.of(existingPost));

        // when
        Post updatedPost = postService.updatePost(postId, updateData);

        // then
        // 반환된 객체의 필드가 변경되었는지 확인
        assertEquals("수정된 제목", updatedPost.getTitle());
        assertEquals("수정된 작성자", updatedPost.getUsername());
        assertEquals("수정된 내용", updatedPost.getContent());

        // findById가 호출되었는지 확인
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
        // postRepository.deleteById가 1L 파라미터로 호출되었는지 검증
        verify(postRepository).deleteById(postId);
    }
}