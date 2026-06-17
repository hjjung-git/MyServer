package com.example.my_server.controller;

import com.example.my_server.repository.ArticleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ArticleController {

    private final ArticleRepository articleRepository;

    public ArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @GetMapping("/panel/news")
    public String panelNews(@RequestParam(value = "page", defaultValue = "0") int page,
                            Model model) {
        model.addAttribute("articles",
            articleRepository.findAllByOrderByPublishedAtDesc(PageRequest.of(page, 20)));
        return "fragments/panel-news :: news";
    }
}
