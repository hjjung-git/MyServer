package com.example.my_server.service;

import com.example.my_server.domain.Post;
import com.example.my_server.domain.PostType;
import com.example.my_server.domain.Role;
import com.example.my_server.domain.TradePosition;
import com.example.my_server.domain.User;
import com.example.my_server.exception.PostNotFoundException;
import com.example.my_server.repository.PostRepository;
import com.example.my_server.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException; // 추가
import java.math.BigDecimal;
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

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;

    @BeforeEach
    void setUp()
    {
        post = new Post("테스트 제목", "테스터", "테스트 내용");

        User user = new User("testUser", "password", Role.USER);
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown()
    { SecurityContextHolder.clearContext(); }

    @Test
    @DisplayName("게시글 저장 - 성공")
    void savePost_Success() throws IOException // ✅ throws IOException 추가
    {
        // given
        User user = new User("testUser", "password", Role.USER);
        given(userRepository.findByLoginId(any())).willReturn(Optional.of(user));

        // postRepository 저장 로직
        given(postRepository.save(any(Post.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Long savedId = postService.save(post, null);

        // then
        verify(userRepository).findByLoginId(any());
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
    void updatePost_Success() throws IOException
    {
        // given
        Long postId = 1L;
        User owner = new User("user", "password", Role.USER);
        owner.setLoginId("user"); // SecurityContext principal name과 일치
        Post existingPost = new Post("기존 제목", "user", "기존 내용");
        existingPost.setUser(owner);
        Post updateData = new Post("수정된 제목", "수정된 작성자", "수정된 내용");

        given(postRepository.findById(postId)).willReturn(Optional.of(existingPost));
        given(userRepository.findByLoginId("user")).willReturn(Optional.of(owner));

        // when
        Post updatedPost = postService.updatePost(postId, updateData, null);

        // then
        assertEquals("수정된 제목", updatedPost.getTitle());
        assertEquals("수정된 내용", updatedPost.getContent());
        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("매매일지 수익률 자동 계산 - LONG 포지션 수익")
    void savePost_TradeLogLong_CalculatesProfitRate() throws IOException
    {
        // given
        User user = new User("testUser", "password", Role.USER);
        given(userRepository.findByLoginId(any())).willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class))).willAnswer(invocation -> invocation.getArgument(0));

        Post tradeLog = new Post("BTC 롱 진입", "테스터", "내용");
        tradeLog.setType(PostType.TRADE_LOG);
        tradeLog.setPosition(TradePosition.LONG);
        tradeLog.setEntryPrice(new BigDecimal("1000"));
        tradeLog.setExitPrice(new BigDecimal("1100"));

        // when
        postService.save(tradeLog, null);

        // then : (1100 - 1000) / 1000 * 100 = +10.00%
        assertEquals(new BigDecimal("10.00"), tradeLog.getProfitRate());
    }

    @Test
    @DisplayName("매매일지 수익률 자동 계산 - SHORT 포지션은 방향이 반전된다")
    void savePost_TradeLogShort_CalculatesInvertedProfitRate() throws IOException
    {
        // given
        User user = new User("testUser", "password", Role.USER);
        given(userRepository.findByLoginId(any())).willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class))).willAnswer(invocation -> invocation.getArgument(0));

        Post tradeLog = new Post("ETH 숏 진입", "테스터", "내용");
        tradeLog.setType(PostType.TRADE_LOG);
        tradeLog.setPosition(TradePosition.SHORT);
        tradeLog.setEntryPrice(new BigDecimal("1000"));
        tradeLog.setExitPrice(new BigDecimal("900"));

        // when
        postService.save(tradeLog, null);

        // then : 가격은 -10% 하락했지만 SHORT이므로 수익은 +10.00%
        assertEquals(new BigDecimal("10.00"), tradeLog.getProfitRate());
    }

    @Test
    @DisplayName("인사이트 글은 수익률을 계산하지 않는다")
    void savePost_Insight_ProfitRateStaysNull() throws IOException
    {
        // given
        User user = new User("testUser", "password", Role.USER);
        given(userRepository.findByLoginId(any())).willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when : post는 기본 타입(INSIGHT)
        postService.save(post, null);

        // then
        assertNull(post.getProfitRate());
    }

    @Test
    @DisplayName("게시글 삭제 - 성공")
    void deletePost_Success()
    {
        // given
        Long postId = 1L;
        User owner = new User("user", "password", Role.USER);
        owner.setLoginId("user");
        Post existingPost = new Post("테스트 제목", "user", "테스트 내용");
        existingPost.setUser(owner);

        given(postRepository.findById(postId)).willReturn(Optional.of(existingPost));
        given(userRepository.findByLoginId("user")).willReturn(Optional.of(owner));

        // when
        postService.delete(postId);

        // then
        verify(postRepository).deleteById(postId);
    }
}