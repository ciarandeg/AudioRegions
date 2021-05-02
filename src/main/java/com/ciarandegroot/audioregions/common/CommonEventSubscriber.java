package com.ciarandegroot.audioregions.common;

import com.ciarandegroot.audioregions.common.command.AudioRegionsCommand;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonEventSubscriber {
    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
        AudioRegionsCommand.register(commandDispatcher);
    }
}