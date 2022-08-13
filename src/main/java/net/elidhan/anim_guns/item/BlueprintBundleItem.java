package net.elidhan.anim_guns.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BlueprintBundleItem extends Item
{
    public BlueprintBundleItem(Settings settings)
    {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack itemStack = user.getStackInHand(hand);

        for(int i = 0; i < BlueprintItem.BLUEPRINT_ITEM_LIST.size(); i++)
        {
            ItemStack bluePrint = new ItemStack(BlueprintItem.BLUEPRINT_ITEM_LIST.get(i));
            user.giveItemStack(bluePrint);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }
        return TypedActionResult.success(itemStack, world.isClient());
    }
}
