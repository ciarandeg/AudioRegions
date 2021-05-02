package com.ciarandegroot.audioregions.server.meta;

import java.util.HashMap;

public class SongMeta extends HashMap<String, SongMeta.Song> {
    public static class Song {
        private String artist;
        private String artistURL;
        private String title;

        public String getArtist() {
            return artist;
        }

        public String getArtistURL() {
            return artistURL;
        }

        public String getTitle() {
            return title;
        }
    }
}