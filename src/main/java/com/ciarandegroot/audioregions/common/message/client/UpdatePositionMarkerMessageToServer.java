package com.ciarandegroot.audioregions.common.message.client;

import com.ciarandegroot.audioregions.common.CommonUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class UpdatePositionMarkerMessageToServer {
    private static CommonUtils.PositionMarkerType marker;
    private static BlockPos pos;

    public UpdatePositionMarkerMessageToServer(CommonUtils.PositionMarkerType markerType, BlockPos blockPos) {
        marker = markerType;
        pos = blockPos;
    }

    public CommonUtils.PositionMarkerType getMarkerType() {
        return marker;
    }

    public BlockPos getBlockPos() {
        return pos;
    }

    public static UpdatePositionMarkerMessageToServer decode(PacketBuffer buf) {
        marker = buf.readEnumValue(CommonUtils.PositionMarkerType.class);
        pos = buf.readBlockPos();
        return new UpdatePositionMarkerMessageToServer(marker, pos);
    }

    public void encode(PacketBuffer buf) {
        buf.writeEnumValue(marker);
        buf.writeBlockPos(pos);
    }
}