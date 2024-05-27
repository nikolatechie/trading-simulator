package com.nikolagrujic.tradingsimulator.controller;

import com.nikolagrujic.tradingsimulator.dto.ErrorResponse;
import com.nikolagrujic.tradingsimulator.dto.NewsArticleCommentDto;
import com.nikolagrujic.tradingsimulator.dto.NewsArticleDto;
import com.nikolagrujic.tradingsimulator.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/news")
public class NewsController {
    private final NewsService newsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<?> getNewsArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
            Page<NewsArticleDto> articles = newsService.getNewsArticles(pageable);
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            LOGGER.error("Couldn't retrieve news articles: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                new ErrorResponse(
                    e.getMessage()
                )
            );
        }
    }

    @PostMapping
    public ResponseEntity<?> flipLike(@RequestParam Long articleId) {
        newsService.flipLike(articleId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/comment")
    public ResponseEntity<?> addComment(@RequestBody NewsArticleCommentDto commentDto) {
        newsService.addComment(commentDto);
        return ResponseEntity.ok().build();
    }
}