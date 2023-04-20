package com.nikolagrujic.tradingsimulator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewsListResponse {
    private String status;
    private int totalResults;
    private List<NewsArticle> articles;
}

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class ArticleSource {
    private String id;
    private String name;
}