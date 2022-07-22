package net.elidhan.anim_guns.mixin.client;

import com.mojang.authlib.GameProfile;
import net.elidhan.anim_guns.item.gun.GunTemplateItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity
{
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile)
    {
        super(world, pos, yaw, profile);
    }
    @Inject(method = "getFovMultiplier", at = @At("Tail"), cancellable = true)
    public void zoomLevel(CallbackInfoReturnable<Float> ci){
        ItemStack gun = this.getStackInHand(Hand.MAIN_HAND);

        if(gun.getItem() instanceof GunTemplateItem && this.isSneaking() && GunTemplateItem.isLoaded(gun))
        {
            ci.setReturnValue(0.8f);
        }
    }
}
