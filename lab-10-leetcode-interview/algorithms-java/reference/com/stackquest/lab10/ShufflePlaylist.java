package com.stackquest.lab10;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/** REFERENCE / SPOILER. */
class Result {
    private static final class ArtistSongs {
        private final String artist;
        private final int firstPosition;
        private final Queue<List<String>> songs = new ArrayDeque<>();

        private ArtistSongs(String artist, int firstPosition) {
            this.artist = artist;
            this.firstPosition = firstPosition;
        }
    }

    public static List<List<String>> getShuffledPlaylist(List<List<String>> playlist) {
        Map<String, ArtistSongs> byArtist = new LinkedHashMap<>();
        for (int i = 0; i < playlist.size(); i++) {
            List<String> song = playlist.get(i);
            int firstPosition = i;
            byArtist.computeIfAbsent(song.get(1), artist -> new ArtistSongs(artist, firstPosition)).songs.add(song);
        }

        Comparator<ArtistSongs> priority = Comparator
                .<ArtistSongs>comparingInt(artist -> artist.songs.size()).reversed()
                .thenComparingInt(artist -> artist.firstPosition);
        PriorityQueue<ArtistSongs> available = new PriorityQueue<>(priority);
        available.addAll(byArtist.values());

        List<List<String>> shuffled = new ArrayList<>(playlist.size());
        String previousArtist = null;
        while (!available.isEmpty()) {
            ArtistSongs first = available.remove();
            ArtistSongs selected = first;
            if (first.artist.equals(previousArtist) && !available.isEmpty()) {
                selected = available.remove();
                available.add(first);
            }

            shuffled.add(selected.songs.remove());
            previousArtist = selected.artist;
            if (!selected.songs.isEmpty()) {
                available.add(selected);
            }
        }
        return shuffled;
    }
}
