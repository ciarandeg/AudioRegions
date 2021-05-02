package com.ciarandegroot.audioregions.client.player;

import com.ciarandegroot.audioregions.client.ClientUtils;

import java.util.Timer;
import java.util.TimerTask;

public class Fader {
    private static final long FADE_DURATION = 2000;
    private static final long FADE_TICK_DURATION = 20;
    private static final float MIN_VOLUME = 0.0f;
    private static final Timer FADE_TIMER = new Timer();
    private static IncrementFadeTask incrementFadeTask = new IncrementFadeTask();
    private static boolean isFading = false;

    protected static void requestFade() {
        if (!isFading) startFade();
    }

    protected static void cancelFade() {
        isFading = false;
        incrementFadeTask.cancel();
        resetVolume();
        incrementFadeTask = new IncrementFadeTask();
    }

    protected static void updateVolume() {
        if (!isFading()) resetVolume();
    }

    protected static boolean isFading() {
        return isFading;
    }

    protected static boolean isFinished() {
        return isFading && getVolume() == MIN_VOLUME;
    }

    private static float getVolume() {
        return MusicPlayer.current.getSourceVolume();
    }

    private static void setVolume(float volume) {
        MusicPlayer.current.setSourceVolume(volume);
        MusicPlayer.pending.setSourceVolume(volume);
    }

    private static void resetVolume() {
        setVolume(ClientUtils.getVolume());
    }

    private static void startFade() {
        isFading = true;
        FADE_TIMER.scheduleAtFixedRate(incrementFadeTask, 0, FADE_TICK_DURATION);
    }

    private static void incrementFade() {
        float volume = getVolume();
        if (volume > MIN_VOLUME) {
            float fadeVolumePerTick = ((float) FADE_TICK_DURATION) / ((float) FADE_DURATION);
            fadeVolumePerTick *= ClientUtils.getVolume();
            setVolume(Math.max(MIN_VOLUME, volume - fadeVolumePerTick));
        }
    }

    private static class IncrementFadeTask extends TimerTask {
        @Override
        public void run() {
            incrementFade();
        }
    }
}