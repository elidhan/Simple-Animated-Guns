package net.elidhan.anim_guns.item;

import net.elidhan.anim_guns.screen.BlueprintScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.*;

public class BlueprintItem extends Item
{
    public static final List<BlueprintItem> BLUEPRINT_ITEM_LIST = new ArrayList<>();
    public BlueprintItem(Settings settings)
    {
        super(settings);
        BLUEPRINT_ITEM_LIST.add(this);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack itemStack = user.getStackInHand(hand);

        user.openHandledScreen(createScreenHandlerFactory());
        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public boolean hasRecipeRemainder()
    {
        return true;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack)
    {
        return new ItemStack(ModItems.BLUEPRINT_BUNDLE);
    }

    private NamedScreenHandlerFactory createScreenHandlerFactory()
    {
        return new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerEntity) ->
                new BlueprintScreenHandler(syncId, playerInventory), new TranslatableText("container.blueprints")
        );
    }
}
