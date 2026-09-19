package com.stackquest.lab10;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@Tag("I6")
class ContentRecommendationTest {
    @Test
    @Tag("public")
    void ordersTheDocumentedExample() {
        assertEquals(
                List.of("API", "Docker", "Jazz", "Piano", "Yoga", "Running"),
                Solutions.getRecommendations(
                        List.of("Backend", "Music"),
                        List.of(300, 120),
                        List.of("Docker", "Jazz", "API", "Yoga", "Piano", "Running"),
                        List.of("Backend", "Music", "Backend", "Fitness", "Music", "Health")));
    }

    @Test
    @Tag("public")
    void sortsTitlesInsideAOnlyCategory() {
        assertEquals(
                List.of("Alpha", "Middle", "Zulu"),
                Solutions.getRecommendations(
                        List.of("Docs"), List.of(1),
                        List.of("Zulu", "Alpha", "Middle"),
                        List.of("Docs", "Docs", "Docs")));
    }

    @Test
    @Tag("evaluator")
    void ordersAllWatchedCategoriesByRelevanceRatherThanInputOrder() {
        assertEquals(
                List.of("AOne", "ATwo", "Bee", "Sea"),
                Solutions.getRecommendations(
                        List.of("C", "A", "B"), List.of(10, 90, 40),
                        List.of("Sea", "ATwo", "Bee", "AOne"),
                        List.of("C", "A", "B", "A")));
    }

    @Test
    @Tag("evaluator")
    void ordersOnlyUnwatchedCategoriesAndTheirTitlesAlphabetically() {
        assertEquals(
                List.of("Alpha", "Beta", "Moon", "Sun"),
                Solutions.getRecommendations(
                        List.of("History"), List.of(500),
                        List.of("Sun", "Beta", "Moon", "Alpha"),
                        List.of("Zulu", "Alpha", "Zulu", "Alpha")));
    }

    @Test
    @Tag("evaluator")
    void handlesManyItemsWithoutRepeatedLinearHistorySearches() {
        int size = 200_000;
        List<String> watched = new ArrayList<>(size);
        List<Integer> relevance = new ArrayList<>(size);
        List<String> titles = new ArrayList<>(size);
        List<String> categories = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            watched.add("C" + i);
            relevance.add(i + 1);
            titles.add("T" + i);
            categories.add("C" + i);
        }

        List<String> result = assertTimeoutPreemptively(
                Duration.ofSeconds(5),
                () -> Solutions.getRecommendations(watched, relevance, titles, categories));

        assertEquals(size, result.size());
        assertEquals("T199999", result.get(0));
        assertEquals("T0", result.get(size - 1));
    }
}
