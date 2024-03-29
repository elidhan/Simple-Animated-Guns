package net.elidhan.anim_guns.mixin;

import net.elidhan.anim_guns.item.GunItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin
{
    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"))
    private void stopReloadBeforeDrop(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> ci)
    {
        if(stack.getItem() instanceof GunItem)
        {
            stack.getOrCreateNbt().putInt("reloadTick", 0);
            stack.getOrCreateNbt().putBoolean("isReloading", false);
        }
    }
}
