package com.example.my_server.controller;

import com.example.my_server.service.PostService;
import com.example.my_server.domain.Post;
import com.example.my_server.domain.PostType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class PostController
{
    private final PostService postService;

    public PostController(PostService postService)
    { this.postService = postService; }

    @GetMapping("/")
    public String index(Model model)
    { return "redirect:/main/list"; }

    @GetMapping("/main/list")
    public String list(Model model,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "type", required = false) PostType type,
                       @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable)
    {
        // 1. Service를 통해 타입에 맞는 포스트 목록을 가져옴
        Page<Post> postsPage = postService.list(keyword, type, pageable);

        // 2. 가져온 포스트 목록을 View에 전달
        model.addAttribute("posts", postsPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        model.addAttribute("activeMenu", activeMenuFor(type));

        // 3. list.html을 렌더링
        return "list";
    }

    @GetMapping("/post/detail/{id}")
    public String detail(@PathVariable Long id, Model model)
    {
        // 1. Repository를 통해 ID에 해당하는 포스트 찾기
        Post post = postService.findById(id);

        // 2. 조회된 Page 객체를 "post"라는 이름으로 HTML에 전달
        model.addAttribute("post", post);
        model.addAttribute("activeMenu", activeMenuFor(post.getType()));

        // 3. 화면을 그릴 HTML 파일의 이름을 반환
        return "detail";
    }

    private String activeMenuFor(PostType type)
    {
        if (type == null) return "dashboard";
        return type == PostType.TRADE_LOG ? "tradelog" : "insight";
    }

    // 쓰기 기능
    @PostMapping("/post/write")
    public String writePost(@Valid Post post,
                            BindingResult bindingResult,
                            @RequestParam(value = "file", required = false) MultipartFile file,
                            Model model) throws IOException
    {
        if (bindingResult.hasErrors())
        {
            System.out.println("검증 에러 발생 : " + bindingResult.getAllErrors());
            model.addAttribute("activeMenu", activeMenuFor(post.getType()));
            return "write-form";
        }

        postService.save(post, file);
        return "redirect:/main/list";
    }

    // 글쓰기 폼 페이지
    @GetMapping("/post/write/form")
    public String writeForm(Model model)
    {
        model.addAttribute("post", new Post());
        model.addAttribute("activeMenu", "insight");
        return "write-form";
    }

    // 수정 폼 보여주기
    @GetMapping("/post/edit/{id}")
    public String editForm(@PathVariable Long id, Model model)
    {
        // 1. Service를 통해 수정할 포스트를 찾음
        Post post = postService.findById(id);

        // 2. 찾은 포스트를 "post"라는 이름으로 View에 전달 (폼에 기존 데이터가 채워짐)
        model.addAttribute("post", post);
        model.addAttribute("activeMenu", activeMenuFor(post.getType()));

        // 3. edit.html을 렌더링
        return "edit";
    }

    // 수정 처리 기능
    @PostMapping("/post/update/{id}")
    public String update(@PathVariable Long id,
                         @Valid Post post,
                         BindingResult bindingResult,
                         @RequestParam("file") MultipartFile file,
                         Model model) throws IOException
    {
        if (bindingResult.hasErrors())
        {
            model.addAttribute("activeMenu", activeMenuFor(post.getType()));
            return "edit";
        }

        postService.updatePost(id, post, file);
        return "redirect:/post/detail/" + id;
    }

    // 삭제 기능
    @GetMapping("/post/delete/{id}")
    public String deletePost(@PathVariable Long id)
    {
        postService.delete(id);
        return "redirect:/main/list";
    }
}