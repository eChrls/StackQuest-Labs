package com.stackquest.lab10;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/*
 * SHUFFLE PLAYLIST
 *
 * A playlist contains n songs. Every song is represented as [title, artist].
 * Return the same songs in a new playback order using these rules:
 *
 * 1. At each step, choose an artist with the largest number of unplayed songs.
 * 2. Break count ties by the artist's earliest appearance in the input.
 * 3. Do not choose the previously played artist while another artist still
 *    has an unplayed song.
 * 4. Repeating the previous artist is allowed when no alternative remains.
 * 5. Preserve the original relative order of songs by the same artist.
 * 6. Every input song must appear exactly once in the result.
 *
 * Example:
 * playlist = [[FirstLight, Nova], [SecondWind, Nova], [BlueSky, Orbit],
 *             [NightDrive, Nova], [FinalCall, Nova], [OceanView, Pulse]]
 * result   = [[FirstLight, Nova], [BlueSky, Orbit], [SecondWind, Nova],
 *             [OceanView, Pulse], [NightDrive, Nova], [FinalCall, Nova]]
 *
 * Constraints:
 * - 1 <= n <= 200,000
 * - every item contains exactly [title, artist]
 * - titles and artists are alphanumeric and at most 20 characters long
 * - avoid scanning every artist again for every song
 */
class Result {
    /*
     * Complete the 'getShuffledPlaylist' function below.
     *
     * The function is expected to return a 2D_STRING_ARRAY.
     * The function accepts 2D_STRING_ARRAY playlist as parameter.
     */
    public static List<List<String>> getShuffledPlaylist(List<List<String>> playlist) {
        // Write your code here
        throw new UnsupportedOperationException("Implement I7");
    }
}

/* Local runner. Input: n, then n lines containing: title artist. */
class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String firstLine = reader.readLine();
        if (firstLine == null) {
            return;
        }

        int playlistRows = Integer.parseInt(firstLine.trim());
        List<List<String>> playlist = new ArrayList<>(playlistRows);
        for (int i = 0; i < playlistRows; i++) {
            StringTokenizer tokens = new StringTokenizer(reader.readLine());
            playlist.add(List.of(tokens.nextToken(), tokens.nextToken()));
        }

        List<List<String>> result = Result.getShuffledPlaylist(playlist);
        String outputPath = System.getenv("OUTPUT_PATH");
        BufferedWriter writer = outputPath == null || outputPath.isBlank()
                ? new BufferedWriter(new OutputStreamWriter(System.out))
                : new BufferedWriter(new FileWriter(outputPath));
        for (List<String> song : result) {
            writer.write(song.get(0));
            writer.write(' ');
            writer.write(song.get(1));
            writer.newLine();
        }
        writer.flush();
        if (outputPath != null && !outputPath.isBlank()) {
            writer.close();
        }
        reader.close();
    }
}
