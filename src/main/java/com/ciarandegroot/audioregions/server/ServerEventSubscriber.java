package com.ciarandegroot.audioregions.server;

import com.ciarandegroot.audioregions.AudioRegions;
import com.ciarandegroot.audioregions.common.MutablePair;
import com.ciarandegroot.audioregions.common.message.server.UpdateRegionMessageToClient;
import com.ciarandegroot.audioregions.common.region.WorldRegions;
import com.ciarandegroot.audioregions.server.capability.CapabilityProviderRegions;
import com.ciarandegroot.audioregions.server.capability.CapabilityRegions;
import com.ciarandegroot.audioregions.server.nbt.RegionsNBTStorage;
import com.ciarandegroot.audioregions.server.player.PlayerController;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class ServerEventSubscriber {
    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<World> event) {
        event.addCapability(new ResourceLocation(AudioRegions.MOD_ID, "regions"), new CapabilityProviderRegions());
    }

    @SubscribeEvent
    // When player logs in, make sure JSON data has been parsed, and send data through to player
    public void sendPlayerRegionBounds(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        ServerUtils.PLAYER_POSITION_MARKERS.putIfAbsent(player, new MutablePair<>(null, null));
        ServerUtils.PLAYER_CONTROLLERS.putIfAbsent(player, new PlayerController(player));

        WorldRegions worldRegions = ServerUtils.getWorldRegions(event.getEntity().getEntityWorld());
        ListNBT lnbt = (ListNBT) new RegionsNBTStorage().writeNBT(CapabilityRegions.CAPABILITY_REGIONS, worldRegions, null);

        for (INBT inbt : lnbt) {
            AudioRegions.simpleChannel.send(
                    PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                    new UpdateRegionMessageToClient((CompoundNBT) inbt));
        }
    }
}