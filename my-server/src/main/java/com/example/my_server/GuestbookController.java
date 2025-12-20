package com.example.my_server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GuestbookController
{
    // 필드를 final로 만들어 한번 설정되면 변경되지 않음을 보장
    private final GuestbookRepository guestbookRepository;

    // Spring이 이 클래스를 만들 때, 생성자를 통해 GuestbookRepository 객체를 주입해줌
    @Autowired
    public GuestbookController(GuestbookRepository guestbookRepository)
    {
        this.guestbookRepository = guestbookRepository;
    }

    // 방명록 글쓰기 기능 (예: /guestbook/write?author=홍길동&content=안녕하세요)
    @GetMapping("/guestbook/write")
    public Guestbook write(@RequestParam String author, @RequestParam String content)
    {
        Guestbook guestbook = new Guestbook();
        guestbook.setAuthor(author);
        guestbook.setContent(content);

        // Repository를 통해 DB에 저장
        return guestbookRepository.save(guestbook);
    }

    // 방명록 목록 보기 기능
    @GetMapping("/guestbook/list")
    public List<Guestbook> list()
    {
        // Repository를 통해 DB에서 모든 데이터를 조회
        return guestbookRepository.findAll();
    }
}