package com.ciarandegroot.audioregions.server.player;

import com.ciarandegroot.audioregions.common.region.RegionData;
import com.ciarandegroot.audioregions.common.region.WorldRegions;
import com.ciarandegroot.audioregions.server.meta.SongMeta;
import com.ciarandegroot.audioregions.server.player.util.ChatFormatter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public class PlayerChatView {
    protected final PlayerEntity OWNER;

    public PlayerChatView(PlayerEntity owner) {
        this.OWNER = owner;
    }

    public void sendError(PlayerController.ErrorType type) {
        String result;

        switch (type) {
            case REGION_DOES_NOT_EXIST:
                result = "Requested region does not exist";
                break;
            case REGION_ALREADY_EXISTS:
                result = "Requested region name is already in use";
                break;
            case NO_REGION_SELECTED:
                result = "Your position markers have not been set";
                break;
            case INVALID_PLAYLIST_SIZE:
                result = "Requested an invalid playlist size. Try switching playlist type";
                break;
            case SONG_OUT_OF_BOUNDS:
                result = "Requested song position is greater than playlist size";
                break;
            default:
                result = "UNDEFINED";
        }

        sendChatMessage(ChatFormatter.formatError(result));
    }

    public void nowPlaying(@Nullable SongMeta.Song song) {
        IFormattableTextComponent msg = new TranslationTextComponent("");

        if (song == null) {
            msg.append(ChatFormatter.formatArtistName("NOTHING"));
        } else {
            IFormattableTextComponent artist = ChatFormatter.formatArtistName(song.getArtist());
            IFormattableTextComponent dash = ChatFormatter.formatBody(" - ");
            IFormattableTextComponent title = ChatFormatter.formatSongTitle(song.getTitle());

            if (song.getArtistURL() != null) {
                artist.mergeStyle(Style.EMPTY
                        .setUnderlined(true)
                        .setClickEvent(new ClickEvent(
                                ClickEvent.Action.OPEN_URL,
                                song.getArtistURL()))
                        .setHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new StringTextComponent("Check out the artist!")))
                );
            }

            msg.append(artist).append(dash).append(title);
        }

        sendChatMessage(msg);
    }

    public void updatePosMarker1() {
        updatePosMarker(1);
    }

    public void updatePosMarker2() {
        updatePosMarker(2);
    }

    private void updatePosMarker(int num) {
        sendChatMessage(ChatFormatter.formatBody("Set position marker " + num));
    }

    public void listRegions(WorldRegions worldRegions) {
        IFormattableTextComponent msg = ChatFormatter.formatRegionName("Regions: ");

        if (worldRegions.isEmpty())
            msg.append(ChatFormatter.formatBody("NONE"));
        else {
            int i = 0;
            for (String name : worldRegions.keySet()) {
                msg.append(ChatFormatter.formatRegionName(name));
                if (i < worldRegions.keySet().size() - 1)
                    msg.append(ChatFormatter.formatBody(", "));
                ++i;
            }
        }

        sendChatMessage(msg);
    }

    public void syncMetadata(String playerName) {
        sendChatMessage(emptyTextComponent()
                .append(ChatFormatter.formatBody("Synced server metadata to " + playerName)));
    }

    public void createRegion(String regionName, int priority) {
        sendChatMessage(emptyTextComponent()
                .append(ChatFormatter.formatBody("Created region "))
                .append(ChatFormatter.formatRegionName(regionName))
                .append(ChatFormatter.formatBody(", priority "))
                .append(ChatFormatter.formatPriority(priority)));
    }

    public void destroyRegion(String regionName) {
        sendChatMessage(emptyTextComponent()
                .append(ChatFormatter.formatBody("Destroyed region "))
                .append(ChatFormatter.formatRegionName(regionName)));
    }

    public void showRegionInfo(String regionName, int regionPriority, RegionData.PlaylistType playlistType, List<String> songIDs) {
        IFormattableTextComponent songList = emptyTextComponent();

        for (int i = 0; i < songIDs.size(); ++i) {
            songList.append(ChatFormatter.formatBody("\n  " + (i + 1) + ". "));
            songList.append(ChatFormatter.formatSongID(songIDs.get(i)));
        }

        sendChatMessage(emptyTextComponent()
                .append(ChatFormatter.formatBody("\nRegion name: "))
                .append(ChatFormatter.formatRegionName(regionName))
                .append(ChatFormatter.formatBody("\nRegion priority: "))
                .append(ChatFormatter.formatPriority(regionPriority))
                .append(ChatFormatter.formatBody("\nPlaylist type: "))
                .append(ChatFormatter.formatPlaylistType(playlistType))
                .append(ChatFormatter.formatBody("\nSongs: "))
                .append(songList)
        );
    }

    public void setRegionPriority(String regionName, int priority) {
        sendChatMessage(emptyTextComponent()
                .append(ChatFormatter.formatBody("Changed region "))
                .append(ChatFormatter.formatRegionName(regionName))
                .append(ChatFormatter.formatBody(ChatFormatter.makePossessive(regionName) + " priority to "))
                .append(ChatFormatter.formatPriority(priority)));
    }

    public void setRegionPlaylistType(String regionName, RegionData.PlaylistType type) {
        sendChatMessage(emptyTextComponent()
                .append(ChatFormatter.formatBody("Changed region "))
                .append(ChatFormatter.formatRegionName(regionName))
                .append(ChatFormatter.formatBody(ChatFormatter.makePossessive(regionName) + " playlist type to "))
                .append(ChatFormatter.formatPlaylistType(type)));
    }

    public void addRegionPlaylistSong(String regionName, String songID) {
        sendChatMessage(emptyTextComponent()
                .append(ChatFormatter.formatBody("Added song "))
                .append(ChatFormatter.formatSongID(songID))
                .append(ChatFormatter.formatBody(" to region "))
                .append(ChatFormatter.formatRegionName(regionName)));
    }

    public void removeRegionPlaylistSong(String regionName, String songID) {
        sendChatMessage(emptyTextComponent()
                .append(ChatFormatter.formatBody("Removed song "))
                .append(ChatFormatter.formatSongID(songID))
                .append(ChatFormatter.formatBody(" from region "))
                .append(ChatFormatter.formatRegionName(regionName)));
    }

    private void sendChatMessage(ITextComponent msg) {
        OWNER.sendMessage(new TranslationTextComponent("chat.type.announcement",
                ChatFormatter.formatModID("AudioRegions"),
                msg), UUID.randomUUID());
    }

    private static TranslationTextComponent emptyTextComponent() {
        return new TranslationTextComponent("");
    }
}