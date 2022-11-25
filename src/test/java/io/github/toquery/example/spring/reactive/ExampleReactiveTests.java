package io.github.toquery.example.spring.reactive;

import io.github.toquery.example.spring.reactive.model.Feed;
import io.github.toquery.example.spring.reactive.model.FeedPhoto;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 *
 */
public class ExampleReactiveTests {

    @Test
    public void mergeFluxes() {
        Flux<String> characterFlux = Flux
                .just("Garfield", "Kojak", "Barbossa")
                .delayElements(Duration.ofMillis(500));

        Flux<String> foodFlux = Flux
                .just("Lasagna", "Lollipops", "Apples")
                .delaySubscription(Duration.ofMillis(250))
                .delayElements(Duration.ofMillis(500));

        Flux<String> mergedFlux = characterFlux.mergeWith(foodFlux);

        StepVerifier.create(mergedFlux)
                .expectNext("Garfield")
                .expectNext("Lasagna")
                .expectNext("Kojak")
                .expectNext("Lollipops")
                .expectNext("Barbossa")
                .expectNext("Apples")
                .verifyComplete();
    }

    @Test
    public void zipFluxes() {
        Flux<String> characterFlux = Flux.just("Garfield", "Kojak");
        Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples");

        Flux<Tuple2<String, String>> zippedFlux = Flux.zip(characterFlux, foodFlux);

        zippedFlux.log().subscribe(System.out::println);

//        StepVerifier.create(zippedFlux)
//                .expectNextMatches(p ->
//                        p.getT1().equals("Garfield") &&
//                                p.getT2().equals("Lasagna"))
//                .expectNextMatches(p ->
//                        p.getT1().equals("Kojak") &&
//                                p.getT2().equals("Lollipops"))
//                .expectNextMatches(p ->
//                        p.getT1().equals("Barbossa") &&
//                                p.getT2().equals("Apples"))
//                .verifyComplete();
    }

    @Test
    public void zipFluxesToObject() {
        Flux<String> characterFlux = Flux.just("Garfield", "Kojak");
        Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples");

        Flux<String> zippedFlux = Flux.zip(characterFlux, foodFlux,
                (c, f) -> c + " eats " + f);

        zippedFlux.log().subscribe(System.out::println);
//        StepVerifier.create(zippedFlux)
//                .expectNext("Garfield eats Lasagna")
//                .expectNext("Kojak eats Lollipops")
//                .expectNext("Barbossa eats Apples")
//                .verifyComplete();
    }

    @Test
    public void testZipWith() {
        Flux.just("a", "b").zipWith(Flux.just("c", "d", "e")).subscribe(System.out::println);

        Flux.just("a", "b").zipWith(Flux.just("c", "d", "e"), (s1, s2) -> String.format("%s-%s", s1, s2)).subscribe(System.out::println);

//        [a,c]
//        [b,d]
//        a-c
//        b-d
    }
    @Test
    public void combineLatest(){
        Flux.combineLatest(Flux.just(1, 2), Flux.just("c", "d", "d"), (s1, s2) -> String.format("%s-%s", s1, s2)).subscribe(System.out::println);
    }

    @Test
    public void testReduce() {
        Flux.range(1, 100).reduce((x, y) -> x + y).subscribe(System.out::println);
        Flux.range(1, 100).reduceWith(() -> 100, (x, y) -> x + y).subscribe(System.out::println);
    }


    @Test
    public void testMerge() {
        Flux.merge(Flux.just(1, 2), Flux.just("a", "b", "c")).log().subscribe(System.out::println);
        Flux.mergeSequential(Flux.just(1, 2), Flux.just("a", "b", "c")).log().subscribe(System.out::println);
    }

    @Test
    public void testFlatMap() {
        Flux.just(1, 2).flatMap(x -> Flux.just(x + "3", "4")).toStream().forEach(System.out::println);
    }

    @Test
    public void testConcatMap() {
        Flux.just(1, 2).concatMap(x -> Flux.just(x + "3", "4")).toStream().forEach(System.out::println);
    }

    @Test
    public void mergeStream() {
        Flux<Feed> feedFlux = Flux.just(
                Feed.builder().id("1").feedText("feed1").createTime(LocalDateTime.now()).build(),
                Feed.builder().id("2").feedText("feed2").createTime(LocalDateTime.now()).build(),
                Feed.builder().id("3").feedText("feed3").createTime(LocalDateTime.now()).build()
        );

        Flux<FeedPhoto> feedPhotoFlux = Flux.just(
                FeedPhoto.builder().id("1").feedId("1").url("url1").build(),
                FeedPhoto.builder().id("2").feedId("1").url("url2").build(),
                FeedPhoto.builder().id("3").feedId("1").url("url3").build(),
                FeedPhoto.builder().id("4").feedId("2").url("url4").build(),
                FeedPhoto.builder().id("5").feedId("2").url("url5").build(),
                FeedPhoto.builder().id("6").feedId("3").url("url6").build()
        );


        Mono<Map<String, Collection<FeedPhoto>>> collectMultiMap = feedPhotoFlux.collectMultimap(FeedPhoto::getFeedId);
//                feedPhotoFlux.collectMultimap(FeedPhoto::getFeedId).doOnNext(item -> item.get("")).subscribe(System.out::println);
        Map<String, Collection<FeedPhoto>> map = collectMultiMap.block();

        feedFlux.map(feed -> {
//            collectMultiMap.doOnNext(map -> feed.setPhotos(map.get(feed.getId()))).subscribe();
            feed.setPhotos(map.get(feed.getId()));

            return feed;
        }).subscribe(System.out::println);

//        Flux.concat(feedPhotoFlux).w, (feed, map) -> {
//            Collection<FeedPhoto> feedPhotos = map.get(feed.getId());
//             feed.setPhotos(feedPhotos);
//            return feed;
//        }).subscribe(System.out::println);

//        Flux<Tuple2<Integer, String>> correctWayOfZippingFluxToMono = feedFlux
//                .flatMap(userId -> Mono.just(userId)
//                        .zipWith(collectMultiMap, (feed, photos) -> new Tuple2(feed.getId(), photos.get(feed.getId())))));


//        feedFlux.flatMap(feed -> Flux.zip(feed, collectMultiMap)
//                .flatMapMany(tuple -> Flux.fromIterable(numbers).map(i -> Tuples.of(i,tuple))))

//        feedFlux.zipWith(collectMultiMap).subscribe(System.out::println);
//
//        feedFlux.zipWith(collectMultiMap, (feedResponse, feedPhotoMap) -> {
//            feedResponse.setPhotos(feedPhotoMap.get(feedResponse.getId()));
//            return feedResponse;
//        }).log().subscribe(System.out::println);

    }
}
