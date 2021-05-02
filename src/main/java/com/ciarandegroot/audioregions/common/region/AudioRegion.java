package com.ciarandegroot.audioregions.common.region;

import com.ciarandegroot.audioregions.AudioRegions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AudioRegion {
    private static final String DIR_PATH = "sounds/music/";
    private static final String FILE_EXTENSION = ".ogg";
    public final ResourceLocation[] RESOURCE_LOCATIONS;
    private final List<AxisAlignedBB> BOUNDS;
    public final RegionData.PlaylistType PLAYLIST_TYPE;
    private final int PRIORITY;
    private final String DIMENSION;

    public AudioRegion(RegionData region) {
        BOUNDS = region.getBounds();
        PRIORITY = region.getPriority();
        PLAYLIST_TYPE = region.getPlaylistType();
        DIMENSION = region.dimension;

        List<String> songIDs = region.getSongIDs();
        RESOURCE_LOCATIONS = new ResourceLocation[songIDs.size()];
        for (int i = 0; i < songIDs.size(); ++i) {
            String rlPath = DIR_PATH + songIDs.get(i) + FILE_EXTENSION;
            RESOURCE_LOCATIONS[i] = new ResourceLocation(AudioRegions.MOD_ID, rlPath);
        }
    }

    public AudioRegion() {
        RESOURCE_LOCATIONS = new ResourceLocation[0];
        PRIORITY = 0;
        PLAYLIST_TYPE = RegionData.PlaylistType.ORDERED;
        DIMENSION = "overworld";
        BOUNDS = null;
    }

    public boolean containsPlayer() {
        if (BOUNDS == null) return false;
        PlayerEntity player = Minecraft.getInstance().player;

        Vector3d playerPos = player.getPositionVec();
        playerPos = new Vector3d(Math.floor(playerPos.x), playerPos.y, Math.floor(playerPos.z));

        for (AxisAlignedBB box : BOUNDS)
            if (playerPos.x >= box.minX && playerPos.x <= box.maxX &&
                    playerPos.y >= box.minY && playerPos.y <= box.maxY &&
                    playerPos.z >= box.minZ && playerPos.z <= box.maxZ)
                return true;
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AudioRegion region = (AudioRegion) o;
        return PRIORITY == region.PRIORITY &&
                Arrays.equals(RESOURCE_LOCATIONS, region.RESOURCE_LOCATIONS) &&
                Objects.equals(BOUNDS, region.BOUNDS) &&
                PLAYLIST_TYPE == region.PLAYLIST_TYPE &&
                Objects.equals(DIMENSION, region.DIMENSION);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(BOUNDS, PLAYLIST_TYPE, PRIORITY, DIMENSION);
        result = 31 * result + Arrays.hashCode(RESOURCE_LOCATIONS);
        return result;
    }

    public String getDimension() {
        return DIMENSION;
    }

    public int getPriority() {
        return PRIORITY;
    }
}