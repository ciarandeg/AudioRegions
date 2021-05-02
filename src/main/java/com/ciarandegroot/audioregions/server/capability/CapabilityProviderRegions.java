package com.ciarandegroot.audioregions.server.capability;

import com.ciarandegroot.audioregions.AudioRegions;
import com.ciarandegroot.audioregions.common.region.WorldRegions;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityProviderRegions implements ICapabilitySerializable<INBT> {
    private static final String REGION_NBT = "region";
    private static final Direction NO_SPECIFIC_SIDE = null;
    private static final byte COMPOUND_NBT_ID = new CompoundNBT().getId();
    private static final WorldRegions WORLD_REGIONS = new WorldRegions();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (CapabilityRegions.CAPABILITY_REGIONS == cap) {
            return (LazyOptional<T>) LazyOptional.of(() -> WORLD_REGIONS);
        }
        return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        INBT regionNBT = CapabilityRegions.CAPABILITY_REGIONS.writeNBT(WORLD_REGIONS, NO_SPECIFIC_SIDE);
        nbt.put(REGION_NBT, regionNBT);
        return nbt;
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        if (nbt.getId() != COMPOUND_NBT_ID) {
            AudioRegions.LOGGER.error("Unexpected NBT type: " + nbt);
            return;
        }

        CompoundNBT compoundNBT = (CompoundNBT) nbt;
        INBT regionNBT = compoundNBT.get(REGION_NBT);
        CapabilityRegions.CAPABILITY_REGIONS.readNBT(WORLD_REGIONS, NO_SPECIFIC_SIDE, regionNBT);
    }
}