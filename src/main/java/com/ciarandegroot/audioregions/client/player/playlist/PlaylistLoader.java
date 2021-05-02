package com.ciarandegroot.audioregions.client.player.playlist;

import com.ciarandegroot.audioregions.client.player.playlist.song.SongLoader;
import com.ciarandegroot.audioregions.client.song.SongStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

public class PlaylistLoader {
    private final PlaylistPlayer PLAYER;
    private final Queue<Integer> OUT_QUEUE;
    private final PlaylistSongDispenser SONG_DISPENSER;
    private final HashMap<SongStream, List<Integer>> LOADED_SONG_BUFFERS;
    private final HashMap<Integer, SongStream> BUFFER_SONG;
    private PlaylistLoaderState state;
    private SongStream loadingSong;

    // A state machine that loads its Playlist's Songs at appropriate times and supplies their output.
    //   Needs to be ticked via the update() method. Loaded buffers can be polled from the internal queue
    //   using hasBuffers() and pollBuffer().
    public PlaylistLoader(PlaylistPlayer player) {
        this.PLAYER = player;
        SONG_DISPENSER = new PlaylistSongDispenser(player.PLAYLIST);
        OUT_QUEUE = new LinkedList<>();
        LOADED_SONG_BUFFERS = new HashMap<>();
        BUFFER_SONG = new HashMap<>();
    }

    public void update() {
        if (state == null) enterStateUntilSettled(new LoadState());

        PlaylistLoaderState updateState = state.update();
        if (updateState != null) enterStateUntilSettled(updateState);
    }

    public void cancel() throws IOException {
        if (state != null) state.cancel();
        if (loadingSong != null) loadingSong.unload();
    }

    public boolean hasBuffers() {
        return !OUT_QUEUE.isEmpty();
    }

    public Integer pollBuffer() {
        return OUT_QUEUE.poll();
    }

    public SongStream songOfBuffer(Integer buf) {
        return BUFFER_SONG.get(buf);
    }

    private void enterStateUntilSettled(PlaylistLoaderState newState) {
        state = newState;
        PlaylistLoaderState nextState = state.enter();
        if (nextState != null) enterStateUntilSettled(nextState);
    }

    private class LoadState extends PlaylistLoaderState {
        private final ConcurrentLinkedQueue<Integer> loadedBlocks;
        private final List<Integer> blockBuffer; // gets added to LOADED_SONG_BUFFERS once loaded
        private Future<?> loadingFuture;

        public LoadState() {
            loadedBlocks = new ConcurrentLinkedQueue<>();
            blockBuffer = new ArrayList<>();
        }

        @Override
        PlaylistLoaderState enter() {
            if (PLAYER.isPlaylistEmpty())
                return new IdleState();

            loadingSong = SONG_DISPENSER.dispense();

            // if song already fully loaded, dump song into OUT_QUEUE
            if (LOADED_SONG_BUFFERS.containsKey(loadingSong)) {
                OUT_QUEUE.addAll(LOADED_SONG_BUFFERS.get(loadingSong));
                return new GenSilenceState(getRandomDelayVal());
            } else {
                loadingFuture = SongLoader.load(loadingSong, loadedBlocks);
                return null;
            }
        }

        @Override
        PlaylistLoaderState update() {
            while (!loadedBlocks.isEmpty()) {
                Integer block = loadedBlocks.poll();
                OUT_QUEUE.add(block);
                BUFFER_SONG.put(block, loadingSong);
                blockBuffer.add(block);
            }

            if (loadingFuture.isDone()) {
                LOADED_SONG_BUFFERS.put(loadingSong, blockBuffer);
                return new GenSilenceState(getRandomDelayVal());
            }

            return null;
        }

        @Override
        void cancel() {
            if (loadingFuture != null)
                loadingFuture.cancel(true);
        }

        private int getRandomDelayVal() {
            int min = PLAYER.PLAYLIST.plInfo.songDelayMin;
            int max = PLAYER.PLAYLIST.plInfo.songDelayMax;

            if (max <= 0) return 0;

            long diff = max - min;
            double pct = Math.random();
            return min + (int) (diff * pct);
        }
    }

    private class GenSilenceState extends PlaylistLoaderState {
        final int DURATION;

        public GenSilenceState(int duration) {
            DURATION = duration;
        }

        @Override
        PlaylistLoaderState enter() {
            return update();
        }

        @Override
        PlaylistLoaderState update() {
            if (DURATION > 0) OUT_QUEUE.add(SongLoader.generateSilence(DURATION));
            return new IdleState();
        }

        @Override
        void cancel() {
        }
    }

    private class IdleState extends PlaylistLoaderState {
        @Override
        PlaylistLoaderState enter() {
            return update();
        }

        @Override
        PlaylistLoaderState update() {
            int songCount = SONG_DISPENSER.getNumSongsDispensed();
            if (songCount == 0) return null;

            boolean playbackIsCaughtUp = PLAYER.getSongsCompleted() + 1 >= songCount;
            if (playbackIsCaughtUp)
                return new LoadState();
            return null;
        }

        @Override
        void cancel() {
        }
    }
}