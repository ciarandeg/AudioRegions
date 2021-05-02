package com.ciarandegroot.audioregions.common.message.handler;

import com.ciarandegroot.audioregions.AudioRegions;
import com.ciarandegroot.audioregions.client.ClientUtils;
import com.ciarandegroot.audioregions.client.region.RegionChecker;
import com.ciarandegroot.audioregions.common.message.client.SupplySongMetaMessageToServer;
import com.ciarandegroot.audioregions.common.message.server.RemoveRegionMessageToClient;
import com.ciarandegroot.audioregions.common.message.server.RequestSongMetaMessageToClient;
import com.ciarandegroot.audioregions.common.message.server.UpdateRegionMessageToClient;

import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class MessageHandlerOnClient {
    public static void onMessageReceived(final UpdateRegionMessageToClient message,
                                         Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();

        // ensure that this code is running on the client side
        if (sideReceived != LogicalSide.CLIENT) {
            AudioRegions.LOGGER.warn("SupplyRegionsMessageToClient was invalid" + message.toString());
            return;
        }

        // assume ctxHandler is a client
        Optional<ClientWorld> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
        if (!clientWorld.isPresent()) {
            AudioRegions.LOGGER.warn("SupplyRegionsMessageToClient context could not provide a ClientWorld.");
            return;
        }

        ctx.enqueueWork(() -> processMessage(message));
        ctx.setPacketHandled(true);
    }

    private static void processMessage(UpdateRegionMessageToClient message) {
        RegionChecker.addRegion(message.getRegionName(), message.getRegion());
    }

    public static void onMessageReceived(final RemoveRegionMessageToClient message,
                                         Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();

        // ensure that this code is running on the client side
        if (sideReceived != LogicalSide.CLIENT) {
            AudioRegions.LOGGER.warn("SupplyRegionsMessageToClient was invalid" + message.toString());
            return;
        }

        // assume ctxHandler is a client
        Optional<ClientWorld> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
        if (!clientWorld.isPresent()) {
            AudioRegions.LOGGER.warn("SupplyRegionsMessageToClient context could not provide a ClientWorld.");
            return;
        }

        ctx.enqueueWork(() -> processMessage(message));
        ctx.setPacketHandled(true);
    }

    private static void processMessage(RemoveRegionMessageToClient message) {
        RegionChecker.removeRegion(message.getRegionName());
    }

    public static void onMessageReceived(final RequestSongMetaMessageToClient message,
                                         Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();

        // ensure that this code is running on the client side
        if (sideReceived != LogicalSide.CLIENT) {
            AudioRegions.LOGGER.warn("SupplyRegionsMessageToClient was invalid" + message.toString());
            return;
        }

        // assume ctxHandler is a client
        Optional<ClientWorld> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
        if (!clientWorld.isPresent()) {
            AudioRegions.LOGGER.warn("SupplyRegionsMessageToClient context could not provide a ClientWorld.");
            return;
        }

        ctx.enqueueWork(() -> processMessage(message));
        ctx.setPacketHandled(true);
    }

    private static void processMessage(RequestSongMetaMessageToClient message) {
        AudioRegions.simpleChannel.sendToServer(new SupplySongMetaMessageToServer(ClientUtils.getSongMetaJson()));
    }

    public static boolean isThisProtocolAcceptedByClient(String protocolVersion) {
        return AudioRegions.MESSAGE_PROTOCOL_VERSION.equals(protocolVersion);
    }
}