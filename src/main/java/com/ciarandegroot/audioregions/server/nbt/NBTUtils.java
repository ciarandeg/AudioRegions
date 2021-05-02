package com.ciarandegroot.audioregions.server.nbt;

import com.ciarandegroot.audioregions.common.region.RegionBundle;
import com.ciarandegroot.audioregions.common.region.RegionData;
import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.List;

import static com.ciarandegroot.audioregions.server.nbt.NBTTags.*;

public class NBTUtils {
    public static CompoundNBT regionBundleToCompound(RegionBundle bundle) {
        RegionData data = bundle.getSecond();

        CompoundNBT cnbt = new CompoundNBT();
        cnbt.put(REGION_NAME, StringNBT.valueOf(bundle.getFirst()));
        cnbt.put(REGION_PRIORITY, IntNBT.valueOf(data.getPriority()));
        cnbt.put(REGION_PLAYLIST_TYPE, StringNBT.valueOf(data.getPlaylistType().toString()));

        ListNBT lnbt = new ListNBT();
        for (String id : data.getSongIDs()) {
            lnbt.add(StringNBT.valueOf(id));
        }
        cnbt.put(REGION_SONG_IDS, lnbt);

        List<Pair<BlockPos, BlockPos>> cornerPairs = data.getCornerPairs();
        cnbt.put(REGION_CORNER1, LongNBT.valueOf(cornerPairs.get(0).getFirst().toLong()));
        cnbt.put(REGION_CORNER2, LongNBT.valueOf(cornerPairs.get(0).getSecond().toLong()));
        return cnbt;
    }

    public static RegionBundle compoundToRegionBundle(CompoundNBT cnbt) {
        RegionBundle region = new RegionBundle(cnbt.getString(REGION_NAME),
                cnbt.getInt(REGION_PRIORITY),
                RegionData.PlaylistType.valueOf(cnbt.getString(REGION_PLAYLIST_TYPE)),
                BlockPos.fromLong(cnbt.getLong(REGION_CORNER1)),
                BlockPos.fromLong(cnbt.getLong(REGION_CORNER2)));

        ListNBT ids = cnbt.getList(REGION_SONG_IDS, Constants.NBT.TAG_STRING);
        for (INBT id : ids) region.getSecond().addSong(id.getString());
        return region;
    }
}