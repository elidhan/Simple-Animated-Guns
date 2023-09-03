package net.elidhan.anim_guns.mixin;

import net.elidhan.anim_guns.attribute.GunAttributes;
import net.elidhan.anim_guns.item.GunItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Final
    @Shadow
    private static EntityAttributeModifier SPRINTING_SPEED_BOOST;

    @Shadow
    public abstract ItemStack getMainHandStack();

    @Shadow
    @Nullable
    public abstract EntityAttributeInstance getAttributeInstance(EntityAttribute attribute);

    @Inject(method = "setSprinting", at = @At("TAIL"))
    public void setSprinting(CallbackInfo ci)
    {
        if(this.getMainHandStack().getItem() instanceof GunItem)
        {
            NbtCompound nbtCompound = this.getMainHandStack().getOrCreateNbt();
            EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);

            if(nbtCompound.getBoolean("isAiming"))
            {
                super.setSprinting(false);
                if(entityAttributeInstance != null) entityAttributeInstance.removeModifier(SPRINTING_SPEED_BOOST);
            }
        }
    }

    @Inject(method = "createLivingAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;", at = @At("RETURN"))
    private static void addAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.getReturnValue().add(GunAttributes.GUN_DAMAGE, 0.0);
    }
}
