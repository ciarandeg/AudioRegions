package com.ciarandegroot.audioregions.server.nbt;

import com.ciarandegroot.audioregions.common.region.RegionBundle;
import com.ciarandegroot.audioregions.common.region.WorldRegions;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class RegionsNBTStorage implements Capability.IStorage<WorldRegions> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<WorldRegions> capability, WorldRegions instance, Direction side) {
        ListNBT nbt = new ListNBT();
        for (String name : instance.keySet()) {
            nbt.add(NBTUtils.regionBundleToCompound(new RegionBundle(name, instance.get(name))));
        }
        return nbt;
    }

    @Override
    public void readNBT(Capability<WorldRegions> capability, WorldRegions instance, Direction side, INBT nbt) {
        if (nbt.getType() == ListNBT.TYPE) {
            ListNBT regionList = (ListNBT) nbt;
            for (INBT region : regionList) {
                RegionBundle bundle = NBTUtils.compoundToRegionBundle((CompoundNBT) region);
                instance.put(bundle.getFirst(), bundle.getSecond());
            }
        }
    }
}