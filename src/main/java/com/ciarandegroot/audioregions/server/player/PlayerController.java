package com.ciarandegroot.audioregions.server.player;

import com.ciarandegroot.audioregions.AudioRegions;
import com.ciarandegroot.audioregions.common.CommonUtils;
import com.ciarandegroot.audioregions.common.message.server.RemoveRegionMessageToClient;
import com.ciarandegroot.audioregions.common.message.server.RequestSongMetaMessageToClient;
import com.ciarandegroot.audioregions.common.message.server.UpdateRegionMessageToClient;
import com.ciarandegroot.audioregions.common.region.RegionBundle;
import com.ciarandegroot.audioregions.common.region.RegionData;
import com.ciarandegroot.audioregions.common.region.WorldRegions;
import com.ciarandegroot.audioregions.server.ServerUtils;
import com.ciarandegroot.audioregions.server.nbt.NBTUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class PlayerController {
    protected final PlayerEntity OWNER;
    private final PlayerModel model;
    private final PlayerChatView view;
    private WorldRegions worldRegions;

    public PlayerController(PlayerEntity owner) {
        this.OWNER = owner;
        this.model = new PlayerModel();
        this.view = new PlayerChatView(owner);
    }

    public void nowPlaying() {
        takeAction(
                () -> null,
                () -> {
                },
                () -> view.nowPlaying(ServerUtils.SONG_META.get(model.curSongID))
        );
    }

    public void setPosMarker(CommonUtils.PositionMarkerType marker, BlockPos pos) {
        switch (marker) {
            case POS1:
                takeAction(() -> null, () -> model.marker1 = pos, view::updatePosMarker1);
                break;
            case POS2:
                takeAction(() -> null, () -> model.marker2 = pos, view::updatePosMarker2);
        }
    }

    public void listRegions() {
        takeAction(
                () -> null,
                () -> {
                },
                () -> view.listRegions(ServerUtils.getWorldRegions(OWNER.getEntityWorld()))
        );
    }

    public void syncMetadata() {
        takeAction(
                () -> null,
                () -> AudioRegions.simpleChannel.send(
                        PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) OWNER),
                        new RequestSongMetaMessageToClient()),
                () -> view.syncMetadata(OWNER.getName().getString())
        );
    }

    public void createRegion(String regionName, int priority) {
        takeAction(
                () -> {
                    if (model.marker1 == null || model.marker2 == null) return ErrorType.NO_REGION_SELECTED;
                    if (worldRegions.containsKey(regionName)) return ErrorType.REGION_ALREADY_EXISTS;
                    return null;
                },
                () -> {
                    worldRegions.put(
                            regionName,
                            new RegionData(priority, RegionData.PlaylistType.ORDERED, model.marker1, model.marker2));
                    updateRegionGlobally(regionName);
                },
                () -> view.createRegion(regionName, priority)
        );
    }

    public void destroyRegion(String regionName) {
        takeAction(
                () -> worldRegions.containsKey(regionName) ? null : ErrorType.REGION_DOES_NOT_EXIST,
                () -> {
                    worldRegions.remove(regionName);
                    ServerUtils.sendMessageToAllPlayersInWorld(
                            OWNER.getEntityWorld(),
                            new RemoveRegionMessageToClient(regionName));
                },
                () -> view.destroyRegion(regionName)
        );
    }

    public void showRegionInfo(String regionName) {
        takeAction(
                () -> worldRegions.containsKey(regionName) ? null : ErrorType.REGION_DOES_NOT_EXIST,
                () -> {
                },
                () -> {
                    RegionData region = worldRegions.get(regionName);
                    view.showRegionInfo(
                            regionName,
                            region.getPriority(),
                            region.getPlaylistType(),
                            region.getSongIDs());
                });
    }

    public void setRegionPriority(String regionName, int priority) {
        takeAction(
                () -> worldRegions.containsKey(regionName) ? null : ErrorType.REGION_DOES_NOT_EXIST,
                () -> {
                    worldRegions.get(regionName).setPriority(priority);
                    updateRegionGlobally(regionName);
                },
                () -> view.setRegionPriority(regionName, priority)
        );
    }

    public void setRegionPlaylistType(String regionName, RegionData.PlaylistType type) {
        takeAction(
                () -> {
                    if (!worldRegions.containsKey(regionName)) return ErrorType.REGION_DOES_NOT_EXIST;

                    int songCount = worldRegions.get(regionName).getSongCount();
                    switch (type) {
                        case SIMPLE_LOOP:
                            if (songCount < 1) return ErrorType.INVALID_PLAYLIST_SIZE;
                        case INTRO_LOOP:
                            if (songCount < 2) return ErrorType.INVALID_PLAYLIST_SIZE;
                    }

                    return null;
                },
                () -> {
                    worldRegions.get(regionName).setPlaylistType(type);
                    updateRegionGlobally(regionName);
                },
                () -> view.setRegionPlaylistType(regionName, type)
        );
    }

    public void addRegionPlaylistSong(String regionName, String songID) {
        takeAction(
                () -> worldRegions.containsKey(regionName) ? null : ErrorType.REGION_DOES_NOT_EXIST,
                () -> {
                    worldRegions.get(regionName).addSong(songID);
                    updateRegionGlobally(regionName);
                },
                () -> view.addRegionPlaylistSong(regionName, songID)
        );
    }

    public void removeRegionPlaylistSong(String regionName, int songPosition) {
        int songIndex = songPosition - 1;
        AtomicReference<String> songName = new AtomicReference<>();
        takeAction(
                () -> {
                    if (!worldRegions.containsKey(regionName)) return ErrorType.REGION_DOES_NOT_EXIST;

                    RegionData region = worldRegions.get(regionName);
                    int songCount = region.getSongCount();
                    if (songIndex >= songCount) return ErrorType.SONG_OUT_OF_BOUNDS;
                    switch (region.getPlaylistType()) {
                        case SIMPLE_LOOP:
                            if (songCount <= 1) return ErrorType.INVALID_PLAYLIST_SIZE;
                            break;
                        case INTRO_LOOP:
                            if (songCount <= 2) return ErrorType.INVALID_PLAYLIST_SIZE;
                    }

                    return null;
                },
                () -> {
                    songName.set(worldRegions.get(regionName).removeSong(songIndex));
                    updateRegionGlobally(regionName);
                },
                () -> view.removeRegionPlaylistSong(regionName, songName.get())
        );
    }

    public void setCurrentSong(String song) {
        takeAction(
                () -> null,
                () -> model.curSongID = song,
                () -> {
                }
        );
    }

    private void updateRegionGlobally(String regionName) {
        CompoundNBT cnbt = NBTUtils.regionBundleToCompound(new RegionBundle(regionName, worldRegions.get(regionName)));
        UpdateRegionMessageToClient message = new UpdateRegionMessageToClient(cnbt);
        ServerUtils.sendMessageToAllPlayersInWorld(OWNER.getEntityWorld(), message);
    }

    private void takeAction(Supplier<ErrorType> checkForErrors, Runnable updateModel, Runnable updateView) {
        worldRegions = ServerUtils.getWorldRegions(OWNER.getEntityWorld());
        ErrorType error = checkForErrors.get();
        if (error == null) {
            updateModel.run();
            updateView.run();
        } else view.sendError(error);
    }

    public enum ErrorType {
        NO_REGION_SELECTED,
        REGION_ALREADY_EXISTS,
        REGION_DOES_NOT_EXIST,
        INVALID_PLAYLIST_SIZE,
        SONG_OUT_OF_BOUNDS
    }
}