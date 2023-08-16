package net.elidhan.anim_guns.mixin.client;

import net.elidhan.anim_guns.item.GunItem;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable
{
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Final
    @Shadow
    private static EntityAttributeModifier SPRINTING_SPEED_BOOST;

    @Shadow
    public abstract ItemStack getMainHandStack();

    @Inject(method = "setSprinting", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void setSprinting(boolean sprinting, CallbackInfo ci, EntityAttributeInstance entityAttributeInstance)
    {
        if(this.getMainHandStack().getItem() instanceof GunItem)
        {
            NbtCompound nbtCompound = this.getMainHandStack().getOrCreateNbt();

            if(nbtCompound.getBoolean("isAiming"))
            {
                super.setSprinting(false);
                entityAttributeInstance.removeModifier(SPRINTING_SPEED_BOOST);
            }
        }
    }
}
