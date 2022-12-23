package net.elidhan.anim_guns.item.inventory;

import net.elidhan.anim_guns.item.BlueprintItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class BlueprintBundleInventory implements Inventory
{
    @Override
    public int size()
    {
        return BlueprintItem.BLUEPRINT_ITEM_LIST.size();
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public ItemStack getStack(int slot)
    {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot, int amount)
    {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot)
    {
        return null;
    }

    @Override
    public void setStack(int slot, ItemStack stack)
    {

    }

    @Override
    public void markDirty()
    {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player)
    {
        return false;
    }

    @Override
    public void clear()
    {

    }
}
