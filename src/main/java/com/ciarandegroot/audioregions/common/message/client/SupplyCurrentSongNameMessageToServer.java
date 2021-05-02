package com.ciarandegroot.audioregions.common.message.client;

import com.ciarandegroot.audioregions.client.ClientEventSubscriber;

import net.minecraft.network.PacketBuffer;

public class SupplyCurrentSongNameMessageToServer {
    private static String songName;

    public SupplyCurrentSongNameMessageToServer() {
    }

    public SupplyCurrentSongNameMessageToServer(String songName) {
        SupplyCurrentSongNameMessageToServer.songName = songName;
    }

    public String getSongName() {
        return songName;
    }

    public static SupplyCurrentSongNameMessageToServer decode(PacketBuffer buf) {
        songName = buf.readString(256);

        return new SupplyCurrentSongNameMessageToServer(songName);
    }

    public void encode(PacketBuffer buf) {
        buf.writeString(ClientEventSubscriber.musicPlayer.nowPlaying());
    }
}