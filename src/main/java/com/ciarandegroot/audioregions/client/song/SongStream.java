package com.ciarandegroot.audioregions.client.song;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.AudioStreamBuffer;
import net.minecraft.client.audio.OggAudioStream;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.sound.sampled.AudioFormat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.openal.AL10.AL_SIZE;
import static org.lwjgl.openal.AL10.alGetBufferi;

public class SongStream {
    public final ResourceLocation SONG;
    protected OggAudioStream ogg;
    private static final int BUFFER_DURATION = 1000; // duration of each buffer block
    private static final int SLEEP_DURATION = 50; // sleep at the end of each load cycle. your cpu deserves it :)

    public SongStream(ResourceLocation song) {
        SONG = song;
    }

    public void load(ConcurrentLinkedQueue<Integer> destination) throws IOException, InterruptedException {
        initOggStream();
        AudioFormat format = ogg.getAudioFormat();
        float bytesPerBuffer = bytesPerBuffer(format);

        while (true) {
            ByteBuffer oggBlock = ogg.readOggSoundWithCapacity((int) bytesPerBuffer);
            AudioStreamBuffer blockBuffer = new AudioStreamBuffer(oggBlock, format);
            OptionalInt pointer = blockBuffer.getUntrackedBuffer();
            int block;

            if (pointer.isPresent()) {
                block = pointer.getAsInt();
                if (alGetBufferi(block, AL_SIZE) == 0) break;
            } else throw new IOException("Couldn't load block from ogg file: " + SONG.getPath());

            destination.add(block);
            Thread.sleep(SLEEP_DURATION); // TODO decide whether to get rid of this
        }
    }

    private void initOggStream() throws IOException {
        IResource resource = Minecraft.getInstance().getResourceManager().getResource(SONG);
        ogg = new OggAudioStream(resource.getInputStream());
    }

    private static float bytesPerBuffer(AudioFormat format) {
        float bytesPerSecond = format.getFrameRate() * format.getFrameSize();
        return bytesPerSecond * ((float) BUFFER_DURATION) / 1000.f;
    }

    public void unload() throws IOException {
        if (ogg != null) ogg.close();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;

        SongStream s = (SongStream) o;
        return SONG.equals(s.SONG);
    }
}