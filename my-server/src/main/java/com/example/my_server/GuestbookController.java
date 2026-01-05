package com.example.my_server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

// @RestController -> @Controller
// 이제 JSON 데이터가 아닌 HTML 파일을 반환
@Controller
public class GuestbookController
{
    private final GuestbookRepository guestbookRepository;

    // 생성자를 통한 객체 주입은 유지
    @Autowired
    public GuestbookController(GuestbookRepository guestbookRepository)
    {
        this.guestbookRepository = guestbookRepository;
    }

    // 홈페이지(/)로 GET 요청이 오면 실행될 메소드
    @GetMapping("/")
    public String index(Model model)
    {
        // 1. DB에서 모든 방명록 데이터를 가져온다.
        List<Guestbook> guestbookList = guestbookRepository.findAll();

        // 2. Model에 "guestbooks"라는 이름으로 데이터를 담아서 HTML로 전달한다.
        model.addAttribute("guestbooks", guestbookList);

        // 3. "list"라는 이름의 HTML 파일을 찾아서 반환하라는 의미.
        return "list";
    }

    @PostMapping("/guestbook/write")
    public String write(@RequestParam String author, @RequestParam String content)
    {
        // 1. Guestbook 객체 생성 및 데이터 설정
        Guestbook guestbook = new Guestbook();
        guestbook.setAuthor(author);
        guestbook.setContent(content);

        // 2. Repository를 통해 DB에 저장
        guestbookRepository.save(guestbook);

        // 3. 글쓰기가 완료되면 홈페이지(/)로 리다이렉트한다.
        return "redirect:/";
    }

    // 방명록 목록 보기 기능
    @GetMapping("/guestbook/list")
    public List<Guestbook> list()
    {
        // Repository를 통해 DB에서 모든 데이터를 조회
        return guestbookRepository.findAll();
    }
}