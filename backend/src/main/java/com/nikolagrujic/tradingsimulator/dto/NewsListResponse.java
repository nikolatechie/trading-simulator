package com.nikolagrujic.tradingsimulator.dto;

import com.nikolagrujic.tradingsimulator.model.NewsArticle;
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
    private List<NewsArticle> articles;
}