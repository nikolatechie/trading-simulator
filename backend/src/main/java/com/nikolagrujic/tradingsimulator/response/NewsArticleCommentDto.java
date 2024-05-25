package com.nikolagrujic.tradingsimulator.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticleCommentDto {
    private String content;
    private String author;
    private LocalDateTime postedDateTime;
}
