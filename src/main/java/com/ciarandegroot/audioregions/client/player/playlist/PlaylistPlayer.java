package com.ciarandegroot.audioregions.client.player.playlist;

import com.ciarandegroot.audioregions.client.ClientUtils;
import com.ciarandegroot.audioregions.client.player.MusicPlayer;
import com.ciarandegroot.audioregions.client.playlist.Playlist;
import com.ciarandegroot.audioregions.client.song.SongStream;

import java.io.IOException;
import java.util.ArrayList;

import static org.lwjgl.openal.AL10.AL_BUFFERS_PROCESSED;
import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetSourcef;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceQueueBuffers;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourceUnqueueBuffers;
import static org.lwjgl.openal.AL10.alSourcef;
import static org.lwjgl.openal.AL10.alSourcei;
import static org.lwjgl.openal.EXTEfx.AL_DIRECT_FILTER;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_NULL;

// Wraps a single playlist and its OpenAL source
//   Source volume is controlled by Fader,
//   Transport is controlled by MusicPlayer.
public class PlaylistPlayer {
    protected final Playlist PLAYLIST;
    private final PlaylistLoader LOADER;
    private final ArrayList<Integer> QUEUED_BUFFERS; // every buffer ever queued to SOURCE_POINTER, in order
    private final Integer SOURCE_POINTER;
    private SongStream playbackSong;
    private int songsFinishedPlaying;

    public PlaylistPlayer(Playlist playlist) {
        PLAYLIST = playlist;
        LOADER = new PlaylistLoader(this);
        QUEUED_BUFFERS = new ArrayList<>();
        SOURCE_POINTER = alGenSources();
        songsFinishedPlaying = 0;
    }

    public void update() {
        if (isPlaying()) updatePlaybackSong();
        if (!isPlaylistEmpty()) LOADER.update();

        while (LOADER.hasBuffers()) queueBuffer(LOADER.pollBuffer());
    }

    public void play() {
        alSourcePlay(SOURCE_POINTER);
    }

    public void stopAndUnload() {
        alSourceStop(SOURCE_POINTER);

        try {
            LOADER.cancel();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[] loadedBuffers = QUEUED_BUFFERS.stream().mapToInt(i -> i).toArray();
        alSourceUnqueueBuffers(SOURCE_POINTER, loadedBuffers);
        alDeleteBuffers(loadedBuffers);
    }

    public void setSourceVolume(float volume) {
        alSourcef(SOURCE_POINTER, AL_GAIN, volume);
    }

    public float getSourceVolume() {
        return alGetSourcef(SOURCE_POINTER, AL_GAIN);
    }

    public void toggleLowPass(boolean enabled) {
        alSourcei(SOURCE_POINTER, AL_DIRECT_FILTER, enabled ? MusicPlayer.lowPassFilter : AL_FILTER_NULL);
    }

    public boolean isPlaying() {
        return alGetSourcei(SOURCE_POINTER, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public boolean isPlaylistEmpty() {
        return PLAYLIST.isEmpty();
    }

    protected int getSongsCompleted() {
        return songsFinishedPlaying;
    }

    public String getCurrentSongName() {
        return (playbackSong == null) ? "EMPTY_SONG" : playbackSong.SONG.getPath();
    }

    protected int getQueuedBufferCount() {
        return QUEUED_BUFFERS.size();
    }

    private void queueBuffer(Integer buf) {
        QUEUED_BUFFERS.add(buf);
        alSourceQueueBuffers(SOURCE_POINTER, buf);
    }

    private void updatePlaybackSong() {
        SongStream oldSong = playbackSong;

        int processedBufCount = alGetSourcei(SOURCE_POINTER, AL_BUFFERS_PROCESSED);
        int currentPlaybackBuf = QUEUED_BUFFERS.get(Math.max(0, processedBufCount));
        playbackSong = LOADER.songOfBuffer(currentPlaybackBuf);

        if (playbackSong != oldSong) {
            ++songsFinishedPlaying;
            ClientUtils.updateCurrentSong();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;

        PlaylistPlayer pp = (PlaylistPlayer) o;
        return PLAYLIST.equals(pp.PLAYLIST);
    }

    @Override
    public String toString() {
        return PLAYLIST.toString();
    }
}