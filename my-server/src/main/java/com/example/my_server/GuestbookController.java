package com.example.my_server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

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
        List<Guestbook> guestbookList = guestbookRepository.findAllByOrderByIdDesc();

        // 2. Model에 "guestbooks"라는 이름으로 데이터를 담아서 HTML로 전달한다.
        model.addAttribute("guestbooks", guestbookList);

        // 3. "list"라는 이름의 HTML 파일을 찾아서 반환하라는 의미.
        return "list";
    }

    // 방명록 목록 보기 기능
    @GetMapping("/guestbook/list")
    public List<Guestbook> list()
    {
        // Repository를 통해 DB에서 모든 데이터를 조회
        return guestbookRepository.findAll();
    }

    // 삭제 기능
    @GetMapping("/guestbook/delete/{id}")
    public String delete(@PathVariable Long id) {
        // Repository를 통해 ID에 해당하는 데이터를 DB에서 삭제
        guestbookRepository.deleteById(id);
        // 삭제 후 홈페이지로 리다이렉트
        return "redirect:/";
    }

    // 수정 폼 보여주기
    @GetMapping("/guestbook/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        // Repository를 통해 ID에 해당하는 기존 데이터를 찾아옴
        Guestbook guestbook = guestbookRepository.findById(id).orElseThrow();
        // 찾아온 객체를 "guestbook"이라는 이름에 담아서 View로 전달
        model.addAttribute("guestbook", guestbook);
        // "edit"라는 이름의 HTML 파일을 찾아서 반환
        return "edit";
    }

    // 쓰기 기능
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

    // 수정 처리 기능
    @PostMapping("/guestbook/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Guestbook guestbook) {
        // DB에서 기존 데이터를 다시 가져옴
        Guestbook existingGuestbook = guestbookRepository.findById(id).orElseThrow();
        // 폼에서 넘어온 데이터로 기존 데이터의 내용을 덮어씀
        existingGuestbook.setAuthor(guestbook.getAuthor());
        existingGuestbook.setContent(guestbook.getContent());
        // 수정된 데이터를 DB에 저장 (JPA는 수정된 것을 인지하고 UPDATE 쿼리를 실행)
        guestbookRepository.save(existingGuestbook);
        // 수정이 완료되면 홈페이지로 리다이렉트
        return "redirect:/";
    }
}