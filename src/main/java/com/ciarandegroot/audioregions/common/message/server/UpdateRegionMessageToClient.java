package com.ciarandegroot.audioregions.common.message.server;

import com.ciarandegroot.audioregions.common.region.AudioRegion;
import com.ciarandegroot.audioregions.common.region.RegionBundle;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.Objects;

public class UpdateRegionMessageToClient {
    private static CompoundNBT serialized;
    private static RegionBundle deserialized;

    public UpdateRegionMessageToClient(CompoundNBT regionTag) {
        serialized = regionTag;
    }

    public UpdateRegionMessageToClient(RegionBundle regionBundle) {
        deserialized = regionBundle;
    }

    public void encode(PacketBuffer buf) {
        buf.writeCompoundTag(serialized);
    }

    public static UpdateRegionMessageToClient decode(PacketBuffer buf) {
        return new UpdateRegionMessageToClient(deserialize(buf));
    }

    // don't call from server side!
    public AudioRegion getRegion() {
        return new AudioRegion(deserialized.getSecond());
    }

    public String getRegionName() {
        return deserialized.getFirst();
    }

    private static RegionBundle deserialize(PacketBuffer buf) {
        return new RegionBundle(Objects.requireNonNull(buf.readCompoundTag()));
    }
}
