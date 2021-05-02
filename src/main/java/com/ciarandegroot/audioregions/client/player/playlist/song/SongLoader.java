package com.ciarandegroot.audioregions.client.player.playlist.song;

import com.ciarandegroot.audioregions.client.song.SongStream;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alGenBuffers;

public class SongLoader {
    public static Future<?> load(SongStream song, ConcurrentLinkedQueue<Integer> outQueue) {
        return ForkJoinPool.commonPool().submit(() -> {
            try {
                song.load(outQueue);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    // Returns a buffer containing the requested duration of silence
    public static int generateSilence(int milliseconds) {
        final int SAMPLES_PER_SECOND = 44100;
        final int SAMPLES_PER_MS = (int) ((float) SAMPLES_PER_SECOND / 1000f);
        int[] buf = new int[1];

        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(SAMPLES_PER_MS * milliseconds * 4);
        BufferUtils.zeroBuffer(byteBuffer);
        alGenBuffers(buf);
        alBufferData(buf[0], AL_FORMAT_STEREO16, byteBuffer, SAMPLES_PER_SECOND);
        return buf[0];
    }
}