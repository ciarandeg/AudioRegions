package com.ciarandegroot.audioregions.server.player.util;

import com.ciarandegroot.audioregions.common.region.RegionData;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ChatFormatter {
    private static final TextFormatting MOD_ID = TextFormatting.GOLD;
    private static final TextFormatting ERROR = TextFormatting.RED;
    private static final TextFormatting BODY = TextFormatting.WHITE;
    private static final TextFormatting REGION_NAME = TextFormatting.BLUE;
    private static final TextFormatting SONG_ID = TextFormatting.DARK_PURPLE;
    private static final TextFormatting PLAYLIST_TYPE = TextFormatting.GREEN;
    private static final TextFormatting REGION_PRIORITY = TextFormatting.DARK_AQUA;
    private static final TextFormatting ARTIST_NAME = TextFormatting.BLUE;
    private static final TextFormatting SONG_TITLE = TextFormatting.LIGHT_PURPLE;

    public static IFormattableTextComponent formatModID(String id) {
        return format(id, MOD_ID);
    }

    public static IFormattableTextComponent formatError(String msg) {
        return format("ERROR: " + msg, ERROR);
    }

    public static IFormattableTextComponent formatBody(String msg) {
        return format(msg, BODY);
    }

    public static IFormattableTextComponent formatRegionName(String name) {
        return format(name, REGION_NAME);
    }

    public static IFormattableTextComponent formatPriority(int priority) {
        return format(Integer.toString(priority), REGION_PRIORITY);
    }

    public static IFormattableTextComponent formatPlaylistType(RegionData.PlaylistType playlistType) {
        return format(playlistType.toString(), PLAYLIST_TYPE);
    }

    public static IFormattableTextComponent formatSongID(String songID) {
        return format(songID, SONG_ID);
    }

    public static IFormattableTextComponent formatArtistName(String artistName) {
        return format(artistName, ARTIST_NAME);
    }

    public static IFormattableTextComponent formatSongTitle(String songTitle) {
        return format(songTitle, SONG_TITLE);
    }

    public static String makePossessive(String word) {
        return word.endsWith("s") ? "'" : "'s";
    }

    private static IFormattableTextComponent format(String msg, TextFormatting format) {
        return new TranslationTextComponent(msg).setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(format)));
    }
}