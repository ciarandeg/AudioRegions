package com.ciarandegroot.audioregions.common.region;

import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class RegionBundle extends Pair<String, RegionData> {
    public RegionBundle(String name, RegionData data) {
        super(name, data);
    }

    public RegionBundle(String name, int priority, RegionData.PlaylistType playlistType, BlockPos corner1, BlockPos corner2) {
        super(name, new RegionData(priority, playlistType, corner1, corner2));
    }

    public RegionBundle(CompoundNBT nbt) {
        super(nbt.getString("region-name"), new RegionData(nbt));
    }

}