package com.ciarandegroot.audioregions.client.playlist;

import com.ciarandegroot.audioregions.client.song.SongStream;

public class SongCharacteristics {
    public final SongStream song;
    public final boolean loop;

    public SongCharacteristics(SongStream song, boolean loop) {
        this.song = song;
        this.loop = loop;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;
        SongCharacteristics sc = (SongCharacteristics) o;
        return song.equals(sc.song) && loop == sc.loop;
    }
}
