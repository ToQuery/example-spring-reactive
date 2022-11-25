package io.github.toquery.example.spring.reactive.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Collection;


/**
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Feed {

    private String id;
    private String feedText;

    private LocalDateTime createTime;

    private Collection<FeedPhoto> photos;

}
