package com.ciarandegroot.audioregions.client.player.playlist;

import com.ciarandegroot.audioregions.client.playlist.Playlist;
import com.ciarandegroot.audioregions.client.playlist.SongCharacteristics;
import com.ciarandegroot.audioregions.client.song.SongStream;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlaylistSongDispenser {
    private final Playlist playlist;
    private final Queue<SongStream> songQueue;
    private int lastQueuedSongIndex = 0;
    private boolean overrideNextLoop = false;
    private int numSongsDispensed;

    protected PlaylistSongDispenser(Playlist playlist) {
        this.playlist = playlist;
        songQueue = new ConcurrentLinkedQueue<>();
        numSongsDispensed = 0;
    }

    protected SongStream dispense() {
        if (songQueue.isEmpty()) generateCycle();
        ++numSongsDispensed;
        return songQueue.poll();
    }

    protected int getNumSongsDispensed() {
        return numSongsDispensed;
    }

    private void generateCycle() {
        // if we're in a loop, keep on looping
        SongCharacteristics lastQueuedSong = playlist.getSong(lastQueuedSongIndex);
        if (lastQueuedSong != null && lastQueuedSong.loop && !overrideNextLoop) {
            songQueue.add(lastQueuedSong.song);
        } else {
            int songCount = playlist.getSongCount();
            List<Integer> songOrder = IntStream.range(0, songCount).boxed().collect(Collectors.toList());
            if (playlist.plInfo.shuffle) Collections.shuffle(songOrder);

            for (int i = 0; i < songCount; ++i) {
                SongCharacteristics sc = playlist.getSong(songOrder.get(i));
                lastQueuedSongIndex = i;
                songQueue.add(sc.song);
                if (sc.loop) break;
            }
        }
    }
}