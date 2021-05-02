package com.ciarandegroot.audioregions.common.command;

import com.ciarandegroot.audioregions.common.CommonUtils.PositionMarkerType;
import com.ciarandegroot.audioregions.common.region.RegionData;
import com.ciarandegroot.audioregions.server.ServerUtils;
import com.ciarandegroot.audioregions.server.player.PlayerController;
import com.ciarandegroot.audioregions.server.player.util.ChatFormatter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.command.EnumArgument;

import java.util.function.Consumer;

public class AudioRegionsCommand {
    private static final int PERMISSION_LEVEL = 0;
    private static final int MIN_PRIORITY = 0;
    private static SuggestionProvider<CommandSource> SONG_ID_SUGGESTIONS;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        if (SONG_ID_SUGGESTIONS == null) SONG_ID_SUGGESTIONS = SuggestionProviders.register(
                new ResourceLocation("song_ids"),
                (context, builder) -> ISuggestionProvider.suggest(
                        ServerUtils.SONG_META.keySet(), builder));

        dispatcher.register(Commands.literal("ar")
                .requires(commandSource -> commandSource.hasPermissionLevel(PERMISSION_LEVEL))
                //.then(Commands.literal("help"))
                .then(Commands.literal("list")
                        //.then(Commands.argument("radius", IntegerArgumentType.integer(0)))
                        .executes(context -> commandWrapper(context.getSource(), PlayerController::listRegions)))
                .then(Commands.literal("now-playing")
                        .executes(context -> commandWrapper(context.getSource(), PlayerController::nowPlaying)))
                .then(Commands.literal("sync-metadata")
                        .executes(context -> commandWrapper(context.getSource(), PlayerController::syncMetadata)))
                .then(Commands.literal("pos1")
                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .executes(context -> commandWrapper(context.getSource(), (controller) -> {
                                    try {
                                        setPosMarker(
                                                controller,
                                                PositionMarkerType.POS1,
                                                BlockPosArgument.getBlockPos(context, "position"));
                                    } catch (CommandSyntaxException e) {
                                        e.printStackTrace();
                                    }
                                })))
                        .executes(context -> commandWrapper(context.getSource(), (controller) -> {
                            setPosMarker(controller, PositionMarkerType.POS1, context.getSource().getEntity().getPosition());
                        })))
                .then(Commands.literal("pos2")
                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .executes(context -> commandWrapper(context.getSource(), (controller) -> {
                                    try {
                                        setPosMarker(
                                                controller,
                                                PositionMarkerType.POS2,
                                                BlockPosArgument.getBlockPos(context, "position"));
                                    } catch (CommandSyntaxException e) {
                                        e.printStackTrace();
                                    }
                                })))
                        .executes(context -> commandWrapper(
                                context.getSource(),
                                (controller) -> setPosMarker(
                                        controller,
                                        PositionMarkerType.POS2,
                                        context.getSource().getEntity().getPosition()))))
                .then(Commands.literal("create")
                        .then(Commands.argument("region-name", StringArgumentType.word())
                                .then(Commands.argument("priority", IntegerArgumentType.integer(MIN_PRIORITY))
                                        .executes(context -> commandWrapper(
                                                context.getSource(),
                                                (controller) -> controller.createRegion(
                                                        StringArgumentType.getString(context, "region-name"),
                                                        IntegerArgumentType.getInteger(context, "priority")))))))
                .then(Commands.literal("destroy")
                        .then(Commands.argument("region-name", StringArgumentType.word())
                                .executes(context -> commandWrapper(
                                        context.getSource(),
                                        (controller) -> controller.destroyRegion(
                                                StringArgumentType.getString(context, "region-name"))))))
                //.then(Commands.literal("rename")
                //        .then(Commands.argument("region-name-from", StringArgumentType.word())
                //                .then(Commands.argument("region-name-to", StringArgumentType.word()))))
                //.then(Commands.literal("overlaps")
                //        .then(Commands.argument("region1", StringArgumentType.word())
                //                .then(Commands.argument("region2", StringArgumentType.word()))))

                .then(Commands.argument("region-name", StringArgumentType.word())
                        //.then(Commands.literal("insert"))
                        //.then(Commands.literal("replace"))
                        //.then(Commands.literal("clear"))
                        .then(Commands.literal("info")
                                .executes(context -> commandWrapper(
                                        context.getSource(),
                                        (controller) -> controller.showRegionInfo(
                                                StringArgumentType.getString(context, "region-name")))))
                        .then(Commands.literal("set-priority")
                                .then(Commands.argument("priority", IntegerArgumentType.integer(MIN_PRIORITY))
                                        .executes(context -> commandWrapper(
                                                context.getSource(),
                                                (controller) -> controller.setRegionPriority(
                                                        StringArgumentType.getString(context, "region-name"),
                                                        IntegerArgumentType.getInteger(context, "priority"))))))
                        .then(Commands.literal("set-type")
                                .then(Commands.argument("playlist-type",
                                        EnumArgument.enumArgument(RegionData.PlaylistType.class))
                                        .executes(context -> commandWrapper(
                                                context.getSource(),
                                                (controller) -> controller.setRegionPlaylistType(
                                                        StringArgumentType.getString(context, "region-name"),
                                                        context.getArgument(
                                                                "playlist-type",
                                                                RegionData.PlaylistType.class))))))
                        .then(Commands.literal("add")
                                .then(Commands.argument("song-id", StringArgumentType.word())
                                        .suggests(SONG_ID_SUGGESTIONS)
                                        .executes(context -> commandWrapper(
                                                context.getSource(),
                                                (controller) -> controller.addRegionPlaylistSong(
                                                        StringArgumentType.getString(context, "region-name"),
                                                        StringArgumentType.getString(context, "song-id"))))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("song-position", IntegerArgumentType.integer(1))
                                        .executes(context -> commandWrapper(
                                                context.getSource(),
                                                (controller) -> controller.removeRegionPlaylistSong(
                                                        StringArgumentType.getString(context, "region-name"),
                                                        IntegerArgumentType.getInteger(context, "song-position"))))))));
    }

    private static int commandWrapper(CommandSource source, Consumer<PlayerController> command) {
        Entity sourceEntity = source.getEntity();

        if (sourceEntity instanceof PlayerEntity) {
            command.accept(ServerUtils.PLAYER_CONTROLLERS.get(sourceEntity));
        } else {
            String result = "Invalid command source. Please run in-game as a player";
            source.sendErrorMessage(ChatFormatter.formatError(result));
        }

        return 1;
    }

    // set pos marker to newPos
    public static void setPosMarker(PlayerController player, PositionMarkerType markerType, BlockPos newPos) {
        player.setPosMarker(markerType, newPos);
    }
}