package com.ciarandegroot.audioregions.client.playlist;

public class PlaylistCharacteristics {
    public final boolean shuffle;
    public final int songDelayMin; // delay between successive songs, in milliseconds
    public final int songDelayMax; // delay between successive songs, in milliseconds

    public PlaylistCharacteristics(boolean shuffle, int songDelayMin, int songDelayMax) {
        this.shuffle = shuffle;
        this.songDelayMin = songDelayMin;
        this.songDelayMax = songDelayMax;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;

        PlaylistCharacteristics pc = (PlaylistCharacteristics) o;
        boolean shuffleMatch = shuffle == pc.shuffle;
        boolean delayMinMatch = songDelayMin == pc.songDelayMin;
        boolean delayMaxMatch = songDelayMax == pc.songDelayMax;
        return shuffleMatch && delayMinMatch && delayMaxMatch;
    }
}
