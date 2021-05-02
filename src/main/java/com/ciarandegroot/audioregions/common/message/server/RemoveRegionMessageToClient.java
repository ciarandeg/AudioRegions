package com.ciarandegroot.audioregions.common.message.server;

import net.minecraft.network.PacketBuffer;

public class RemoveRegionMessageToClient {
    private final String regionName;

    public RemoveRegionMessageToClient(String regionName) {
        this.regionName = regionName;
    }

    public void encode(PacketBuffer buf) {
        buf.writeString(regionName);
    }

    public static RemoveRegionMessageToClient decode(PacketBuffer buf) {
        return new RemoveRegionMessageToClient(buf.readString());
    }

    public String getRegionName() {
        return regionName;
    }
}
