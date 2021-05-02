package com.ciarandegroot.audioregions.client.player;

import com.ciarandegroot.audioregions.client.ClientUtils;
import com.ciarandegroot.audioregions.client.player.playlist.PlaylistPlayer;
import com.ciarandegroot.audioregions.client.playlist.PlaylistCreator;
import com.ciarandegroot.audioregions.common.region.AudioRegion;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_LOWPASS;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_TYPE;
import static org.lwjgl.openal.EXTEfx.AL_LOWPASS_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_LOWPASS_GAINHF;
import static org.lwjgl.openal.EXTEfx.AL_LOWPASS_MAX_GAIN;
import static org.lwjgl.openal.EXTEfx.alFilterf;
import static org.lwjgl.openal.EXTEfx.alFilteri;
import static org.lwjgl.openal.EXTEfx.alGenFilters;

public class MusicPlayer {
    private static final Integer DEVICE_HANDLE = 0xcd; // arbitrary value
    protected static PlaylistPlayer current;
    protected static PlaylistPlayer pending;
    public static int lowPassFilter;
    private static final float LP_CUTOFF = 0.005f;
    private static boolean lowPassEnabled = false;

    public MusicPlayer() {
        initPlaylists();
        initAL();
        updateVolume();
    }

    public void update() {
        // if empty to empty, do nothing
        if (current.isPlaylistEmpty() && pending.isPlaylistEmpty())
            return;

        // if ready to play a new playlist, play from queue
        if (Fader.isFinished() || current.isPlaylistEmpty())
            promotePending();

        current.update();
        pending.update();

        if (!current.isPlaying()) current.play();
    }

    public void request(AudioRegion region) {
        pending = new PlaylistPlayer(PlaylistCreator.fromRegion(region));
        if (pending.equals(current)) Fader.cancelFade();
        else Fader.requestFade();
    }

    public void toggleLowPass(boolean enabled) {
        if (enabled != lowPassEnabled) {
            current.toggleLowPass(enabled);
            pending.toggleLowPass(enabled);
            lowPassEnabled = enabled;
        }
    }

    public void updateVolume() {
        Fader.updateVolume();
    }

    public void unload() {
        Fader.cancelFade();

        current.stopAndUnload();
        if (!current.equals(pending))
            pending.stopAndUnload();

        initPlaylists();
    }

    public String nowPlaying() {
        return current.getCurrentSongName();
    }

    private void promotePending() {
        current.stopAndUnload();
        current = pending;
        if (current.isPlaylistEmpty()) ClientUtils.updateCurrentSong();
        if (Fader.isFading()) Fader.cancelFade();
    }

    private static void initPlaylists() {
        current = new PlaylistPlayer(PlaylistCreator.emptyPlaylist());
        pending = new PlaylistPlayer(PlaylistCreator.emptyPlaylist());
    }

    private static void initAL() {
        String deviceName = alcGetString(DEVICE_HANDLE, ALC_DEFAULT_DEVICE_SPECIFIER);
        ALCCapabilities alcCapabilities = ALC.createCapabilities(alcOpenDevice(deviceName));
        AL.createCapabilities(alcCapabilities);

        lowPassFilter = alGenFilters();
        alFilteri(lowPassFilter, AL_FILTER_TYPE, AL_FILTER_LOWPASS);
        alFilterf(lowPassFilter, AL_LOWPASS_GAIN, AL_LOWPASS_MAX_GAIN);
        alFilterf(lowPassFilter, AL_LOWPASS_GAINHF, LP_CUTOFF);
    }
}