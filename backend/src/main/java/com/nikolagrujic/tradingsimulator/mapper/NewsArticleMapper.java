package com.nikolagrujic.tradingsimulator.mapper;

import com.nikolagrujic.tradingsimulator.model.NewsArticle;
import com.nikolagrujic.tradingsimulator.model.User;
import com.nikolagrujic.tradingsimulator.response.NewsArticleDto;

public class NewsArticleMapper {
    public static NewsArticleDto convertToDto(NewsArticle article, User user) {
        NewsArticleDto dto = new NewsArticleDto();
        dto.setId(article.getId());
        dto.setAuthor(article.getAuthor());
        dto.setTitle(article.getTitle());
        dto.setDescription(article.getDescription());
        dto.setUrl(article.getUrl());
        dto.setUrlToImage(article.getUrlToImage());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setLikeCount(article.getNumberOfLikes());
        dto.setLiked(user.hasLikedArticle(article));
        return dto;
    }
}