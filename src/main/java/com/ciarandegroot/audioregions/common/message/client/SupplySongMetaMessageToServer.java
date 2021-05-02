package com.ciarandegroot.audioregions.common.message.client;

import net.minecraft.network.PacketBuffer;

public class SupplySongMetaMessageToServer {
    private static String json;

    public SupplySongMetaMessageToServer(String metaJson) {
        json = metaJson;
    }

    public String getJson() {
        return json;
    }

    public static SupplySongMetaMessageToServer decode(PacketBuffer buf) {
        int length = buf.readInt();
        json = buf.readString(length);
        return new SupplySongMetaMessageToServer(json);
    }

    public void encode(PacketBuffer buf) {
        buf.writeInt(json.length());
        buf.writeString(json);
    }
}