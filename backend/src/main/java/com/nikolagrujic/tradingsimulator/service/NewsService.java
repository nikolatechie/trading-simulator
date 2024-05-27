package com.nikolagrujic.tradingsimulator.service;

import com.nikolagrujic.tradingsimulator.constants.Constants;
import com.nikolagrujic.tradingsimulator.exception.InvalidArticleException;
import com.nikolagrujic.tradingsimulator.mapper.NewsArticleMapper;
import com.nikolagrujic.tradingsimulator.model.NewsArticle;
import com.nikolagrujic.tradingsimulator.model.NewsArticleComment;
import com.nikolagrujic.tradingsimulator.model.User;
import com.nikolagrujic.tradingsimulator.repository.NewsArticleCommentRepository;
import com.nikolagrujic.tradingsimulator.dto.NewsArticleCommentDto;
import com.nikolagrujic.tradingsimulator.dto.NewsArticleDto;
import com.nikolagrujic.tradingsimulator.dto.NewsListResponse;
import com.nikolagrujic.tradingsimulator.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class NewsService {
    private final NewsRepository newsRepository;
    private final NewsArticleCommentRepository commentRepository;
    private final UserService userService;
    @Value("${news.api.key}")
    private String apiKey;
    private static final String NEWS_ENDPOINT = "https://newsapi.org/v2/everything?q=stock";
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsService.class);
    private static final long PULL_NEWS_PERIOD_MILLISECONDS = 30 * 60 * 1000;
    private static final long DELETE_OLD_NEWS_PERIOD_MILLISECONDS = 12 * 60 * 60 * 1000;
    private static final long DELETE_OLD_NEWS_INITIAL_DELAY_MILLISECONDS = 10000;

    @Autowired
    public NewsService(
            NewsRepository newsRepository,
            UserService userService,
            NewsArticleCommentRepository commentRepository) {
        this.newsRepository = newsRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
    }

    @Async
    @Scheduled(initialDelay = Constants.REQUEST_DELAY_MILLISECONDS, fixedDelay = PULL_NEWS_PERIOD_MILLISECONDS)
    public void startRetrievingNews() {
        // Retrieve news articles every 30 minutes
        try {
            LOGGER.info("Retrieving a list of news articles...");
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Api-Key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.GET,
                    URI.create(NEWS_ENDPOINT + "&from=" + getDateFrom()));
            ResponseEntity<NewsListResponse> responseEntity =
                    restTemplate.exchange(requestEntity, NewsListResponse.class);

            if (responseEntity.hasBody() &&
                    !Objects.requireNonNull(responseEntity.getBody()).getArticles().isEmpty()) {
                List<NewsArticle> newsArticles = responseEntity.getBody().getArticles();
                LOGGER.info("Successfully retrieved a list of {} news articles.", newsArticles.size());
                saveNewsArticles(responseEntity.getBody().getArticles());
            }
        } catch (RestClientException e) {
            LOGGER.error("Couldn't retrieve news articles: {}", e.getMessage());
        }
    }

    private void saveNewsArticles(List<NewsArticle> newsArticles) {
        LOGGER.info("Saving news articles in the database...");
        int invalidNews = 0;
        for (NewsArticle article: newsArticles) {
            try {
                if (!newsRepository.existsByTitle(article.getTitle())) {
                    newsRepository.save(article);
                }
            } catch (Exception e) {
                ++invalidNews;
            }
        }
        if (invalidNews > 0)
            LOGGER.warn("Number of invalid news articles: {}", invalidNews);
    }

    private static String getDateFrom() {
        LocalDate date = LocalDate.now();
        return date.minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public void flipLike(Long articleId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        NewsArticle article = newsRepository.getById(articleId);
        boolean liked = user.hasLikedArticle(article);

        if (liked) {
            LOGGER.info("[{}] Unliking article with id {}.", user.getId(), articleId);
            article.getLikedBy().remove(user);
        } else {
            LOGGER.info("[{}] Liking article with id {}.", user.getId(), articleId);
            article.getLikedBy().add(user);
        }

        newsRepository.save(article);
    }

    @Async
    @Scheduled(initialDelay = DELETE_OLD_NEWS_INITIAL_DELAY_MILLISECONDS,
            fixedDelay = DELETE_OLD_NEWS_PERIOD_MILLISECONDS)
    public void deleteOldNews() {
        LOGGER.info("Deleting old news articles...");
        try {
            newsRepository.deleteAllByPublishedAtBefore(LocalDateTime.now().minusDays(5));
        } catch (Exception e) {
            LOGGER.error("Couldn't delete old news articles: {}", e.getMessage());
        }
    }

    public Page<NewsArticleDto> getNewsArticles(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        Page<NewsArticle> articles = newsRepository.findAll(pageable);
        List<NewsArticleDto> articlesDto = new ArrayList<>();

        for (NewsArticle article: articles.getContent()) {
            articlesDto.add(NewsArticleMapper.convertToDto(article, user));
        }

        return new PageImpl<>(articlesDto, articles.getPageable(), articles.getTotalElements());
    }

    public void addComment(NewsArticleCommentDto commentDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long articleId = commentDto.getArticleId();
        LOGGER.info("[{}] Adding a comment to a news article: {}", email, articleId);
        NewsArticle article = newsRepository.getById(articleId);
        if (article == null) {
            throw new InvalidArticleException("Article with id " + articleId + " does not exist.");
        }
        NewsArticleComment newComment = new NewsArticleComment();
        newComment.setContent(commentDto.getContent());
        newComment.setUser(userService.findByEmail(email));
        newComment.setArticle(article);
        newComment.setPostedDateTime(LocalDateTime.now());
        commentRepository.save(newComment);
    }
}