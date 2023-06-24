package net.elidhan.anim_guns.screen;

import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.item.BlueprintBundleItem;
import net.elidhan.anim_guns.item.BlueprintItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class BlueprintScreenHandler extends ScreenHandler
{
    public BlueprintScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        super(AnimatedGuns.BLUEPRINT_SCREEN_HANDLER_TYPE, syncId);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index)
    {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return player.getMainHandStack().getItem() instanceof BlueprintItem
                || player.getOffHandStack().getItem() instanceof BlueprintItem
                || player.getMainHandStack().getItem() instanceof BlueprintBundleItem
                || player.getOffHandStack().getItem() instanceof BlueprintBundleItem;
    }

}
