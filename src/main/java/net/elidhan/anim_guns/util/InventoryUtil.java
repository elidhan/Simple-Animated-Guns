package net.elidhan.anim_guns.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtil {

    public static int itemCountInInventory(PlayerEntity player, Item item) {

        int itemCount = 0;

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack currentStack = player.getInventory().getStack(i);
            if (!currentStack.isEmpty() && currentStack.isItemEqual(new ItemStack(item))) {
                itemCount += currentStack.getCount();
            }
        }

        return Math.max(itemCount, 0);

    }

    public static void removeItemFromInventory(PlayerEntity player, Item item, int count)
    {
        int itemsToRemove = count;

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack currentStack = player.getInventory().getStack(i);
            if (!currentStack.isEmpty() && currentStack.isItemEqual(new ItemStack(item))) {

                if(currentStack.getCount() > itemsToRemove)
                {
                    currentStack.decrement(itemsToRemove);
                    itemsToRemove = 0;
                    break;
                }
                else
                {
                    itemsToRemove -= currentStack.getCount();
                    currentStack.setCount(0);
                    if(itemsToRemove == 0){break;}

                }

            }

        }

    }

    public static boolean hasPlayerStackInInventory(PlayerEntity player, Item item) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack currentStack = player.getInventory().getStack(i);
            if (!currentStack.isEmpty() && currentStack.isItemEqual(new ItemStack(item))) {
                return true;
            }
        }

        return false;
    }

    public static int getFirstInventoryIndex(PlayerEntity player, Item item) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack currentStack = player.getInventory().getStack(i);
            if (!currentStack.isEmpty() && currentStack.isItemEqual(new ItemStack(item))) {
                return i;
            }
        }

        return -1;
    }
}
