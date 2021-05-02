package com.ciarandegroot.audioregions.client;

import com.ciarandegroot.audioregions.AudioRegions;
import com.ciarandegroot.audioregions.client.player.MusicPlayer;
import com.ciarandegroot.audioregions.client.region.RegionChecker;
import com.ciarandegroot.audioregions.common.region.AudioRegion;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.OptionsSoundsScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.resources.IResource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ClientEventSubscriber {
    public static MusicPlayer musicPlayer;
    private static AudioRegion currentRegion;
    private static final ResourceLocation SONGS = new ResourceLocation(AudioRegions.MOD_ID, "songs.json");

    @SubscribeEvent
    // Block all vanilla music
    public void removeVanillaMusic(PlaySoundEvent event) {
        String name = event.getName();
        String[] tokens = name.split("\\.");
        if (tokens[0].equals("music")) event.setResultSound(null);
    }

    @SubscribeEvent
    // Update RegionEngine whenever the player is updated
    public void updateRegionEngine(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof PlayerEntity && ((PlayerEntity) event.getEntity()).isUser()) {
            if (musicPlayer == null) initMusicPlayer();

            AudioRegion newRegion = RegionChecker.findCurrentRegion();
            if (!newRegion.equals(currentRegion)) {
                currentRegion = newRegion;
                musicPlayer.request(newRegion);
            }

            updateSubmerged(event.getEntity());
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (musicPlayer != null) musicPlayer.update();

        if (ClientUtils.songMetaJson == null) try {
            IResource resource = Minecraft.getInstance().getResourceManager().getResource(SONGS);
            InputStream stream = resource.getInputStream();

            Gson gson = new Gson();
            InputStreamReader reader = new InputStreamReader(stream);
            JsonObject obj = gson.fromJson(reader, JsonObject.class);
            ClientUtils.songMetaJson = gson.toJson(obj);
        } catch (IOException e) {
            AudioRegions.LOGGER.error("Couldn't find " + SONGS);
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    // Free all loaded buffers (and sources) when player logs out of server
    public void unloadSound(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        if (event.getPlayer() == Minecraft.getInstance().player && musicPlayer != null) {
            musicPlayer.unload();
            musicPlayer = null;
            currentRegion = null;
        }
    }

    @SubscribeEvent
    // Update music volume in case volume slider has moved
    public void updateMusicVolume(GuiScreenEvent event) {
        if (Minecraft.getInstance().currentScreen instanceof OptionsSoundsScreen)
            if (musicPlayer != null) musicPlayer.updateVolume();
    }

    private void initMusicPlayer() {
        musicPlayer = new MusicPlayer();
    }

    private void updateSubmerged(Entity player) {
        for (ITag.INamedTag<Fluid> fluid : FluidTags.getAllTags()) {
            if (player.areEyesInFluid(fluid)) {
                musicPlayer.toggleLowPass(true);
                return;
            }
        }
        musicPlayer.toggleLowPass(false);
    }
}