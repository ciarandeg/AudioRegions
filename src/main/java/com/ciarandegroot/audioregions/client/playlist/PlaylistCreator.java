package com.ciarandegroot.audioregions.client.playlist;

import com.ciarandegroot.audioregions.client.song.SongStream;
import com.ciarandegroot.audioregions.common.region.AudioRegion;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PlaylistCreator {
    private static final int DELAY_MIN = 60000;
    private static final int DELAY_MAX = 12000;

    // creates a playlist with optional shuffle, constant songDelay and no song-looping
    private static Playlist simplePlaylist(ResourceLocation[] songs, boolean shuffle) {
        List<SongCharacteristics> songInfo = new ArrayList<>();
        for (ResourceLocation rl : songs) {
            songInfo.add(new SongCharacteristics(new SongStream(rl), false));
        }
        return new Playlist(new PlaylistCharacteristics(shuffle, DELAY_MIN, DELAY_MAX), songInfo);
    }

    public static Playlist simpleOrderedPlaylist(ResourceLocation[] songs) {
        return simplePlaylist(songs, false);
    }

    public static Playlist simpleShuffledPlaylist(ResourceLocation[] songs) {
        return simplePlaylist(songs, true);
    }

    // creates a playlist consisting of one song, which loops
    public static Playlist simpleLoopPlaylist(ResourceLocation song) {
        List<SongCharacteristics> songInfo = new ArrayList<>();
        songInfo.add(new SongCharacteristics(new SongStream(song), true));
        return new Playlist(new PlaylistCharacteristics(false, 0, 0), songInfo);
    }

    // creates a two-song playlist where the first song acts as an intro to the second, which loops
    public static Playlist introLoopPlaylist(ResourceLocation intro, ResourceLocation loop) {
        List<SongCharacteristics> songInfo = new ArrayList<>();
        songInfo.add(new SongCharacteristics(new SongStream(intro), false));
        songInfo.add(new SongCharacteristics(new SongStream(loop), true));
        return new Playlist(new PlaylistCharacteristics(false, 0, 0), songInfo);
    }

    public static Playlist emptyPlaylist() {
        return new Playlist(new PlaylistCharacteristics(false, 0, 0), new ArrayList<>());
    }

    public static Playlist fromRegion(AudioRegion region) {
        Playlist p;
        ResourceLocation[] rls = region.RESOURCE_LOCATIONS;
        switch (region.PLAYLIST_TYPE) {
            case ORDERED:
                p = simpleOrderedPlaylist(rls);
                break;
            case SHUFFLED:
                p = simpleShuffledPlaylist(rls);
                break;
            case SIMPLE_LOOP:
                p = simpleLoopPlaylist(rls[0]);
                break;
            case INTRO_LOOP:
                p = introLoopPlaylist(rls[0], rls[1]);
                break;
            default:
                p = emptyPlaylist();
        }
        return p;
    }
}
