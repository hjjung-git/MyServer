package com.example.my_server.service;

import com.example.my_server.domain.Article;
import com.example.my_server.repository.ArticleRepository;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class NewsService {

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    private final ArticleRepository articleRepository;

    private static final String[] RSS_FEEDS = {
        "https://cointelegraph.com/rss",
        "https://www.coindesk.com/arc/outboundfeeds/rss/",
        "https://decrypt.co/feed"
    };

    public NewsService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Scheduled(fixedDelay = 600_000, initialDelay = 5_000)
    @Transactional
    public void fetchAllFeeds() {
        for (String feedUrl : RSS_FEEDS) {
            try {
                fetchFeed(feedUrl);
            } catch (Exception e) {
                log.warn("RSS 수집 실패 [{}]: {}", feedUrl, e.getMessage());
            }
        }
    }

    private void fetchFeed(String feedUrl) throws Exception {
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(feedUrl)));
        String sourceName = feed.getTitle();

        for (SyndEntry entry : feed.getEntries()) {
            String url = entry.getLink();
            if (url == null || articleRepository.existsByUrl(url)) continue;

            Article article = new Article();
            article.setTitle(entry.getTitle());
            article.setUrl(url);
            article.setSource(sourceName);

            if (entry.getDescription() != null) {
                String raw = entry.getDescription().getValue();
                String plain = raw.replaceAll("<[^>]+>", "").strip();
                article.setSummary(plain.length() > 300 ? plain.substring(0, 300) + "…" : plain);
            }

            LocalDateTime published = entry.getPublishedDate() != null
                ? entry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                : LocalDateTime.now();
            article.setPublishedAt(published);
            article.setFetchedAt(LocalDateTime.now());

            articleRepository.save(article);
        }
        log.info("RSS 수집 완료 [{}]", sourceName);
    }
}
