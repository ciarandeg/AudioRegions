package com.ciarandegroot.audioregions.common.item;

import com.ciarandegroot.audioregions.AudioRegions;
import com.ciarandegroot.audioregions.common.CommonUtils.PositionMarkerType;
import com.ciarandegroot.audioregions.common.MutablePair;
import com.ciarandegroot.audioregions.common.message.client.UpdatePositionMarkerMessageToServer;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SimpleFoiledItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.HashMap;

public class WandItem extends SimpleFoiledItem {
    private static int RETRIGGER_COOLDOWN = 1000;
    private static final HashMap<PositionMarkerType, MutablePair<BlockPos, Long>> PREVIOUS_BLOCK_POS = new HashMap<>();

    public WandItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (!(entity.getEntityWorld() instanceof ServerWorld)) setCorner(PositionMarkerType.POS1);
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (!(world instanceof ServerWorld)) setCorner(PositionMarkerType.POS2);
        return ActionResult.resultConsume(player.getHeldItem(hand));
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return false;
    }

    // MUST BE RUN FROM CLIENT
    private static void setCorner(PositionMarkerType marker) {
        BlockPos pos = getMouseOverBlock();
        if (pos != null) {
            MutablePair<BlockPos, Long> previousPosTimestampPair = PREVIOUS_BLOCK_POS.get(marker);
            boolean shouldUpdate = false;
            long time = getTime();

            if (previousPosTimestampPair == null) shouldUpdate = true;
            else {
                boolean newBlock = !pos.equals(previousPosTimestampPair.getFirst());
                boolean cooldownFinished = time - previousPosTimestampPair.getSecond() >= RETRIGGER_COOLDOWN;
                shouldUpdate = newBlock || cooldownFinished;
            }

            if (shouldUpdate) {
                PREVIOUS_BLOCK_POS.put(marker, new MutablePair<>(pos, time));
                AudioRegions.simpleChannel.sendToServer(new UpdatePositionMarkerMessageToServer(marker, pos));
            }
        }
    }

    private static BlockPos getMouseOverBlock() {
        RayTraceResult trace = Minecraft.getInstance().objectMouseOver;
        if (trace != null && trace.getType() == RayTraceResult.Type.BLOCK)
            return ((BlockRayTraceResult) trace).getPos();
        return null;
    }

    private static long getTime() {
        return System.currentTimeMillis();
    }
}