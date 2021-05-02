package com.ciarandegroot.audioregions.common.region;

import com.ciarandegroot.audioregions.server.nbt.NBTTags;
import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class RegionData {
    public final String dimension = "overworld";

    private int priority;
    private PlaylistType playlistType;
    private final List<String> songIDs = new ArrayList<>();
    private final List<Pair<BlockPos, BlockPos>> cornerPairs = new ArrayList<>();

    public RegionData(int priority, PlaylistType playlistType, BlockPos corner1, BlockPos corner2) {
        this.priority = priority;
        this.playlistType = playlistType;
        cornerPairs.add(new Pair<>(corner1, corner2));
    }

    public RegionData(CompoundNBT nbt) {
        priority = nbt.getInt(NBTTags.REGION_PRIORITY);
        playlistType = PlaylistType.valueOf(nbt.getString(NBTTags.REGION_PLAYLIST_TYPE));
        ListNBT ids = nbt.getList(NBTTags.REGION_SONG_IDS, Constants.NBT.TAG_STRING);
        for (INBT inbt : ids) songIDs.add(inbt.getString());
        cornerPairs.add(new Pair<>(
                BlockPos.fromLong(nbt.getLong(NBTTags.REGION_CORNER1)),
                BlockPos.fromLong(nbt.getLong(NBTTags.REGION_CORNER2))));
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public PlaylistType getPlaylistType() {
        return playlistType;
    }

    public void setPlaylistType(PlaylistType type) {
        this.playlistType = type;
    }

    public List<AxisAlignedBB> getBounds() {
        List<AxisAlignedBB> bbs = new ArrayList<>();
        for (Pair<BlockPos, BlockPos> p : cornerPairs) {
            bbs.add(new AxisAlignedBB(p.getFirst(), p.getSecond()));
        }
        return bbs;
    }

    public List<Pair<BlockPos, BlockPos>> getCornerPairs() {
        return new ArrayList<>(cornerPairs);
    }

    public void addBoundingBox(BlockPos corner1, BlockPos corner2) {
        cornerPairs.add(new Pair<>(corner1, corner2));
    }

    public List<String> getSongIDs() {
        return new ArrayList<>(songIDs);
    }

    public void addSong(String id) {
        songIDs.add(id);
    }

    public String removeSong(int index) {
        return songIDs.remove(index);
    }

    public int getSongCount() {
        return songIDs.size();
    }

    public enum PlaylistType {
        ORDERED,
        SHUFFLED,
        INTRO_LOOP,
        SIMPLE_LOOP,
    }
}
