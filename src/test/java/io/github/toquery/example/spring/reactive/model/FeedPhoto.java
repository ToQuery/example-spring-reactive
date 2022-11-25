package io.github.toquery.example.spring.reactive.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedPhoto {

    private String id;
    private String feedId;
    private String url;
}
