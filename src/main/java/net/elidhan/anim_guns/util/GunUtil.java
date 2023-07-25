package net.elidhan.anim_guns.util;

import mod.azure.azurelib.AzureLibMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class GunUtil {

    /*
     * Call wherever you are firing weapon, making sure to not do it client side.
     */
    public static void spawnLightSource(Entity entity, boolean isInWaterBlock) {
        BlockPos lightBlockPos;

        lightBlockPos = findFreeSpace(entity.getWorld(), entity.getBlockPos(), 2);
        if (lightBlockPos == null)
            return;
        entity.getWorld().setBlockState(lightBlockPos, AzureLibMod.TICKING_LIGHT_BLOCK.getDefaultState());
    }

    private static boolean checkDistance(BlockPos blockPosA, BlockPos blockPosB, int distance) {
        return Math.abs(blockPosA.getX() - blockPosB.getX()) <= distance && Math.abs(blockPosA.getY() - blockPosB.getY()) <= distance && Math.abs(blockPosA.getZ() - blockPosB.getZ()) <= distance;
    }

    private static BlockPos findFreeSpace(World world, BlockPos blockPos, int maxDistance) {
        if (blockPos == null)
            return null;

        int[] offsets = new int[maxDistance * 2 + 1];
        offsets[0] = 0;
        for (int i = 2; i <= maxDistance * 2; i += 2) {
            offsets[i - 1] = i / 2;
            offsets[i] = -i / 2;
        }
        for (int x : offsets)
            for (int y : offsets)
                for (int z : offsets) {
                    BlockPos offsetPos = blockPos.add(x, y, z);
                    BlockState state = world.getBlockState(offsetPos);
                    if (state.isAir() || state.getBlock().equals(AzureLibMod.TICKING_LIGHT_BLOCK))
                        return offsetPos;
                }

        return null;
    }
}
