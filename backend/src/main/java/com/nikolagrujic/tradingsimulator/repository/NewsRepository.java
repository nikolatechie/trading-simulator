package com.nikolagrujic.tradingsimulator.repository;

import com.nikolagrujic.tradingsimulator.model.NewsArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Repository
public interface NewsRepository extends JpaRepository<NewsArticle, Long> {
    boolean existsByTitle(String title);
    @Transactional
    void deleteAllByPublishedAtBefore(LocalDateTime publishedAt);
    Page<NewsArticle> findAll(Pageable pageable);
    NewsArticle getById(Long id);
}