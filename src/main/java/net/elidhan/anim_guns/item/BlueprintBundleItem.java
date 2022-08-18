package net.elidhan.anim_guns.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
            if (user.getInventory().getEmptySlot() > -1)
            {
                user.giveItemStack(bluePrint);
            }
            else
            {
                user.dropItem(bluePrint.getItem());
            }
        }

        user.world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((user.getRandom().nextFloat() - user.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }
        return TypedActionResult.success(itemStack, world.isClient());
    }
}
