package com.ciarandegroot.audioregions.client;

import com.ciarandegroot.audioregions.AudioRegions;
import com.ciarandegroot.audioregions.common.message.client.SupplyCurrentSongNameMessageToServer;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;

public class ClientUtils {
    protected static String songMetaJson;

    // Get Minecraft's music volume (NOT momentary fader volume)
    public static float getVolume() {
        GameSettings settings = Minecraft.getInstance().gameSettings;
        float masterVolume = settings.getSoundLevel(SoundCategory.MASTER);
        float musicVolume = settings.getSoundLevel(SoundCategory.MUSIC);
        return masterVolume * musicVolume;
    }

    public static void updateCurrentSong() {
        AudioRegions.simpleChannel.sendToServer(new SupplyCurrentSongNameMessageToServer());
    }

    public static String getSongMetaJson() {
        return songMetaJson;
    }
}