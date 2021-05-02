package com.ciarandegroot.audioregions.client.region;

import com.ciarandegroot.audioregions.common.region.AudioRegion;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class RegionChecker {
    private static final HashMap<String, AudioRegion> audioRegions = new HashMap<>();

    public static void addRegion(String regionName, AudioRegion region) {
        audioRegions.put(regionName, region);
    }

    public static void removeRegion(String regionName) {
        audioRegions.remove(regionName);
    }

    public static AudioRegion findCurrentRegion() {
        AudioRegion current = new AudioRegion();
        ResourceLocation dimension = Minecraft.getInstance().world.getDimensionKey().getLocation();
        for (AudioRegion r : audioRegions.values()) {
            boolean dimensionMatches = r.getDimension().equals(dimension.getPath()) && r.containsPlayer();
            boolean hasPriority = r.getPriority() >= current.getPriority();
            if (dimensionMatches && hasPriority) current = r;
        }
        return current;
    }
}