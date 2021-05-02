package com.ciarandegroot.audioregions.server.capability;

import com.ciarandegroot.audioregions.common.region.WorldRegions;
import com.ciarandegroot.audioregions.server.nbt.RegionsNBTStorage;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityRegions {
    @CapabilityInject(WorldRegions.class)
    public static Capability<WorldRegions> CAPABILITY_REGIONS = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(
                WorldRegions.class,
                new RegionsNBTStorage(),
                WorldRegions::new);
    }
}
