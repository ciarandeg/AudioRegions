package com.ciarandegroot.audioregions.common.message.server;

import net.minecraft.network.PacketBuffer;

public class RequestSongMetaMessageToClient {

    public RequestSongMetaMessageToClient() {
    }

    public void encode(PacketBuffer buf) {
    }

    public static RequestSongMetaMessageToClient decode(PacketBuffer buf) {
        return new RequestSongMetaMessageToClient();
    }
}
