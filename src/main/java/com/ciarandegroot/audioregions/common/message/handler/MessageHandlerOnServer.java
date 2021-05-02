package com.ciarandegroot.audioregions.common.message.handler;

import com.ciarandegroot.audioregions.AudioRegions;
import com.ciarandegroot.audioregions.common.command.AudioRegionsCommand;
import com.ciarandegroot.audioregions.common.message.client.SupplyCurrentSongNameMessageToServer;
import com.ciarandegroot.audioregions.common.message.client.SupplySongMetaMessageToServer;
import com.ciarandegroot.audioregions.common.message.client.UpdatePositionMarkerMessageToServer;
import com.ciarandegroot.audioregions.server.ServerUtils;
import com.ciarandegroot.audioregions.server.meta.SongMeta;
import com.ciarandegroot.audioregions.server.player.PlayerController;
import com.google.gson.Gson;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageHandlerOnServer {
    public static void onMessageReceived(final SupplyCurrentSongNameMessageToServer message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();

        // ensure that this code is running on the server side
        if (sideReceived != LogicalSide.SERVER) {
            AudioRegions.LOGGER.error("SupplyRegionsMessageToClient was invalid" + message.toString());
            return;
        }

        ServerPlayerEntity sender = ctx.getSender();
        if (sender == null) {
            AudioRegions.LOGGER.error("Failed to supply current song name to server");
            return;
        }

        ctx.enqueueWork(() -> processMessage(sender, message));
        ctx.setPacketHandled(true);
    }

    private static void processMessage(PlayerEntity player, SupplyCurrentSongNameMessageToServer message) {
        String songID = message.getSongName();
        String[] tokens = songID.split("/");
        songID = tokens[tokens.length - 1]; // just get filename, not path
        tokens = songID.split("\\.");
        songID = tokens[0]; // remove trailing .ogg

        AudioRegions.LOGGER.info("MAPPING " + player.getDisplayName().getString() + " TO " + songID);
        PlayerController playerController = ServerUtils.PLAYER_CONTROLLERS.get(player);
        playerController.setCurrentSong(songID);
    }

    public static void onMessageReceived(final UpdatePositionMarkerMessageToServer message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();

        // ensure that this code is running on the server side
        if (sideReceived != LogicalSide.SERVER) {
            AudioRegions.LOGGER.warn("SupplyRegionsMessageToClient was invalid" + message.toString());
            return;
        }

        ctx.enqueueWork(() -> processMessage(message, ctx.getSender()));
        ctx.setPacketHandled(true);
    }

    private static void processMessage(UpdatePositionMarkerMessageToServer message, ServerPlayerEntity player) {
        AudioRegionsCommand.setPosMarker(ServerUtils.PLAYER_CONTROLLERS.get(player), message.getMarkerType(), message.getBlockPos());
    }

    public static void onMessageReceived(final SupplySongMetaMessageToServer message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();

        // ensure that this code is running on the server side
        if (sideReceived != LogicalSide.SERVER) {
            AudioRegions.LOGGER.warn("SupplyRegionsMessageToClient was invalid" + message.toString());
            return;
        }

        ctx.enqueueWork(() -> processMessage(message));
        ctx.setPacketHandled(true);
    }

    private static void processMessage(SupplySongMetaMessageToServer message) {
        ServerUtils.SONG_META = new Gson().fromJson(message.getJson(), SongMeta.class);
    }
}