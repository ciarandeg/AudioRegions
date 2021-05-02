package com.ciarandegroot.audioregions.server;

import com.ciarandegroot.audioregions.AudioRegions;
import com.ciarandegroot.audioregions.common.MutablePair;
import com.ciarandegroot.audioregions.common.message.server.RemoveRegionMessageToClient;
import com.ciarandegroot.audioregions.common.message.server.UpdateRegionMessageToClient;
import com.ciarandegroot.audioregions.common.region.WorldRegions;
import com.ciarandegroot.audioregions.server.capability.CapabilityRegions;
import com.ciarandegroot.audioregions.server.meta.SongMeta;
import com.ciarandegroot.audioregions.server.player.PlayerController;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;

public class ServerUtils {
    public static final HashMap<PlayerEntity, PlayerController> PLAYER_CONTROLLERS = new HashMap<>();
    protected static final HashMap<PlayerEntity, MutablePair<BlockPos, BlockPos>> PLAYER_POSITION_MARKERS = new HashMap<>();
    public static SongMeta SONG_META = new SongMeta();

    // returns the Regions in a given world
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static WorldRegions getWorldRegions(World world) {
        LazyOptional<WorldRegions> opt = world.getCapability(CapabilityRegions.CAPABILITY_REGIONS);
        return opt.resolve().get();
    }

    public static void sendMessageToAllPlayersInWorld(World world, UpdateRegionMessageToClient message) {
        for (PlayerEntity p : world.getPlayers()) {
            PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) p);
            AudioRegions.simpleChannel.send(target, message);
        }
    }

    public static void sendMessageToAllPlayersInWorld(World world, RemoveRegionMessageToClient message) {
        for (PlayerEntity p : world.getPlayers()) {
            PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) p);
            AudioRegions.simpleChannel.send(target, message);
        }
    }

}