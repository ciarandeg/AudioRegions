package com.ciarandegroot.audioregions.client.playlist;

import java.util.List;

public class Playlist {
    public final PlaylistCharacteristics plInfo;
    private final List<SongCharacteristics> songs;

    public Playlist(PlaylistCharacteristics plInfo, List<SongCharacteristics> songs) {
        this.plInfo = plInfo;
        this.songs = songs;
    }

    public boolean isEmpty() {
        return songs.isEmpty();
    }

    public SongCharacteristics getSong(int index) {
        return songs.get(index);
    }

    public int getSongCount() {
        return songs.size();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;

        Playlist p = (Playlist) o;
        boolean songsEqual = songs.equals(p.songs);
        boolean plsEqual = plInfo.equals(p.plInfo);
        return songsEqual && plsEqual;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "EMPTY PLAYLIST";

        String out = "";
        for (SongCharacteristics sc : songs) {
            out = out.concat(sc.song.toString() + ", ");
        }
        return out.substring(0, out.length() - 2);
    }
}
