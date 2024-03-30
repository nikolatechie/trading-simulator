package com.nikolagrujic.tradingsimulator.service;

import com.nikolagrujic.tradingsimulator.constants.Constants;
import com.nikolagrujic.tradingsimulator.model.NewsArticle;
import com.nikolagrujic.tradingsimulator.response.NewsListResponse;
import com.nikolagrujic.tradingsimulator.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class NewsService {
    private final NewsRepository newsRepository;
    @Value("${news.api.key}")
    private String apiKey;
    private static final String NEWS_ENDPOINT = "https://newsapi.org/v2/everything?q=stock";
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsService.class);
    private static final long PULL_NEWS_PERIOD_MILLISECONDS = 30 * 60 * 1000;
    private static final long DELETE_OLD_NEWS_PERIOD_MILLISECONDS = 12 * 60 * 60 * 1000;
    private static final long DELETE_OLD_NEWS_INITIAL_DELAY_MILLISECONDS = 10000;

    @Autowired
    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
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

    public Page<NewsArticle> getNewsArticles(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }
}