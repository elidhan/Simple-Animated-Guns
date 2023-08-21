package net.elidhan.anim_guns.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.elidhan.anim_guns.item.GunItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity
{
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"))
    private void resetGunBeforeDrop(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> ci)
    {
        if(stack.getItem() instanceof GunItem)
        {
            stack.getOrCreateNbt().putInt("reloadTick", 0);
            stack.getOrCreateNbt().putBoolean("isReloading", false);
            stack.getOrCreateNbt().putBoolean("isAiming", false);
        }
    }

    @SuppressWarnings("unused")
    @ModifyExpressionValue(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z", opcode = Opcodes.GETFIELD))
    private boolean swingIfNotHoldingGun(boolean original)
    {
        return original && !(this.getMainHandStack().getItem() instanceof GunItem);
    }

    @SuppressWarnings("all")
    @WrapOperation(method = "attack", constant = @Constant(classValue = SwordItem.class))
    private boolean sweepMeleeIfGun(Object obj, Operation<Boolean> original)
    {
        return original.call(obj) || obj instanceof GunItem;
    }
}
