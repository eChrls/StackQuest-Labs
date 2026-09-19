package com.stackquest.lab10;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@Tag("I7")
class ShufflePlaylistTest {
    @Test
    @Tag("public")
    void shufflesTheDocumentedExample() {
        List<List<String>> playlist = songs(
                "FirstLight", "Nova", "SecondWind", "Nova", "BlueSky", "Orbit",
                "NightDrive", "Nova", "FinalCall", "Nova", "OceanView", "Pulse");

        assertEquals(songs(
                "FirstLight", "Nova", "BlueSky", "Orbit", "SecondWind", "Nova",
                "OceanView", "Pulse", "NightDrive", "Nova", "FinalCall", "Nova"),
                Result.getShuffledPlaylist(playlist));
    }

    @Test
    @Tag("public")
    void preservesOrderWhenThereIsOnlyOneArtist() {
        List<List<String>> playlist = songs("One", "Solo", "Two", "Solo", "Three", "Solo");
        assertEquals(playlist, Result.getShuffledPlaylist(playlist));
    }

    @Test
    @Tag("evaluator")
    void keepsFirstAppearanceOrderWhenEveryArtistHasOneSong() {
        List<List<String>> playlist = songs("C", "Gamma", "A", "Alpha", "B", "Beta");
        assertEquals(playlist, Result.getShuffledPlaylist(playlist));
    }

    @Test
    @Tag("evaluator")
    void breaksEqualCountsByFirstAppearance() {
        assertEquals(
                songs("B1", "B", "A1", "A", "B2", "B", "A2", "A"),
                Result.getShuffledPlaylist(songs("B1", "B", "A1", "A", "A2", "A", "B2", "B")));
    }

    @Test
    @Tag("evaluator")
    void avoidsThePreviousArtistAndAllowsAnInevitableDominantTail() {
        List<List<String>> playlist = songs(
                "A1", "A", "A2", "A", "B1", "B", "A3", "A", "A4", "A", "C1", "C");
        List<List<String>> result = Result.getShuffledPlaylist(playlist);

        assertEquals(songs(
                "A1", "A", "B1", "B", "A2", "A", "C1", "C", "A3", "A", "A4", "A"), result);
        assertNotEquals(result.get(0).get(1), result.get(1).get(1));
        assertPlaylistProperties(playlist, result);
    }

    @Test
    @Tag("evaluator")
    void preservesEveryArtistsRelativeSongOrder() {
        List<List<String>> playlist = songs(
                "A1", "ArtistA", "B1", "ArtistB", "A2", "ArtistA",
                "C1", "ArtistC", "B2", "ArtistB", "A3", "ArtistA");
        assertPlaylistProperties(playlist, Result.getShuffledPlaylist(playlist));
    }

    @Test
    @Tag("evaluator")
    void handlesTwoHundredThousandSongs() {
        List<List<String>> playlist = new ArrayList<>(200_000);
        for (int i = 0; i < 200_000; i++) {
            playlist.add(List.of("T" + i, "A" + (i % 1_000)));
        }

        List<List<String>> result = assertTimeoutPreemptively(
                Duration.ofSeconds(8), () -> Result.getShuffledPlaylist(playlist));

        assertPlaylistProperties(playlist, result);
    }

    private static List<List<String>> songs(String... values) {
        List<List<String>> playlist = new ArrayList<>(values.length / 2);
        for (int i = 0; i < values.length; i += 2) {
            playlist.add(List.of(values[i], values[i + 1]));
        }
        return playlist;
    }

    private static void assertPlaylistProperties(List<List<String>> input, List<List<String>> output) {
        assertEquals(input.size(), output.size());
        assertEquals(frequencies(input), frequencies(output));

        Map<String, List<String>> expectedByArtist = groupedTitles(input);
        assertEquals(expectedByArtist, groupedTitles(output));

        for (int i = 1; i < output.size(); i++) {
            String previous = output.get(i - 1).get(1);
            String current = output.get(i).get(1);
            if (previous.equals(current)) {
                List<List<String>> remaining = output.subList(i, output.size());
                assertEquals(true, remaining.stream().allMatch(song -> song.get(1).equals(current)));
            }
        }
    }

    private static Map<List<String>, Integer> frequencies(List<List<String>> playlist) {
        Map<List<String>, Integer> frequencies = new HashMap<>();
        playlist.forEach(song -> frequencies.merge(song, 1, Integer::sum));
        return frequencies;
    }

    private static Map<String, List<String>> groupedTitles(List<List<String>> playlist) {
        Map<String, List<String>> grouped = new HashMap<>();
        playlist.forEach(song -> grouped.computeIfAbsent(song.get(1), ignored -> new ArrayList<>()).add(song.get(0)));
        return grouped;
    }
}
