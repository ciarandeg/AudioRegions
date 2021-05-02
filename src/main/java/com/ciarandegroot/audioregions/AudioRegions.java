package com.ciarandegroot.audioregions;

import com.ciarandegroot.audioregions.client.ClientEventSubscriber;
import com.ciarandegroot.audioregions.common.CommonEventSubscriber;
import com.ciarandegroot.audioregions.common.item.MusicItems;
import com.ciarandegroot.audioregions.common.message.client.SupplyCurrentSongNameMessageToServer;
import com.ciarandegroot.audioregions.common.message.client.SupplySongMetaMessageToServer;
import com.ciarandegroot.audioregions.common.message.client.UpdatePositionMarkerMessageToServer;
import com.ciarandegroot.audioregions.common.message.handler.MessageHandlerOnClient;
import com.ciarandegroot.audioregions.common.message.handler.MessageHandlerOnServer;
import com.ciarandegroot.audioregions.common.message.server.RemoveRegionMessageToClient;
import com.ciarandegroot.audioregions.common.message.server.RequestSongMetaMessageToClient;
import com.ciarandegroot.audioregions.common.message.server.UpdateRegionMessageToClient;
import com.ciarandegroot.audioregions.server.ServerEventSubscriber;
import com.ciarandegroot.audioregions.server.capability.CapabilityRegions;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

@Mod(AudioRegions.MOD_ID)
public class AudioRegions {
    public static final String MESSAGE_PROTOCOL_VERSION = "1.0";
    public static int CONFIG_MESSAGE_INDEX = 63;
    public static final String MOD_ID = "audioregions";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation simpleChannelRL = new ResourceLocation(MOD_ID, "channel");

    public static SimpleChannel simpleChannel;

    public AudioRegions() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);

        MusicItems.REGISTRY.register(bus);

        ClientEventSubscriber clientSubscriber =
                DistExecutor.callWhenOn(Dist.CLIENT, () -> ClientEventSubscriber::new);
        ServerEventSubscriber serverSubscriber =
                DistExecutor.callWhenOn(Dist.DEDICATED_SERVER, () -> ServerEventSubscriber::new);

        MinecraftForge.EVENT_BUS.register(clientSubscriber == null ? serverSubscriber : clientSubscriber);
        MinecraftForge.EVENT_BUS.register(CommonEventSubscriber.class);
    }

    private void setup(final FMLCommonSetupEvent event) {
        simpleChannel = NetworkRegistry.newSimpleChannel(simpleChannelRL, () -> MESSAGE_PROTOCOL_VERSION,
                MessageHandlerOnClient::isThisProtocolAcceptedByClient,
                (String protocolVersion) -> true);

        simpleChannel.registerMessage(CONFIG_MESSAGE_INDEX++, UpdateRegionMessageToClient.class,
                UpdateRegionMessageToClient::encode, UpdateRegionMessageToClient::decode,
                MessageHandlerOnClient::onMessageReceived,
                Optional.of(PLAY_TO_CLIENT));
        simpleChannel.registerMessage(CONFIG_MESSAGE_INDEX++, RemoveRegionMessageToClient.class,
                RemoveRegionMessageToClient::encode, RemoveRegionMessageToClient::decode,
                MessageHandlerOnClient::onMessageReceived,
                Optional.of(PLAY_TO_CLIENT));
        simpleChannel.registerMessage(CONFIG_MESSAGE_INDEX++, RequestSongMetaMessageToClient.class,
                RequestSongMetaMessageToClient::encode, RequestSongMetaMessageToClient::decode,
                MessageHandlerOnClient::onMessageReceived,
                Optional.of(PLAY_TO_CLIENT));

        simpleChannel.registerMessage(CONFIG_MESSAGE_INDEX++, SupplyCurrentSongNameMessageToServer.class,
                SupplyCurrentSongNameMessageToServer::encode, SupplyCurrentSongNameMessageToServer::decode,
                MessageHandlerOnServer::onMessageReceived,
                Optional.of(PLAY_TO_SERVER));
        simpleChannel.registerMessage(CONFIG_MESSAGE_INDEX++, UpdatePositionMarkerMessageToServer.class,
                UpdatePositionMarkerMessageToServer::encode, UpdatePositionMarkerMessageToServer::decode,
                MessageHandlerOnServer::onMessageReceived,
                Optional.of(PLAY_TO_SERVER));
        simpleChannel.registerMessage(CONFIG_MESSAGE_INDEX++, SupplySongMetaMessageToServer.class,
                SupplySongMetaMessageToServer::encode, SupplySongMetaMessageToServer::decode,
                MessageHandlerOnServer::onMessageReceived,
                Optional.of(PLAY_TO_SERVER));

        CapabilityRegions.register();
    }
}