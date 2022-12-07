package net.elidhan.anim_guns.mixin.client;

import com.mojang.authlib.GameProfile;
import net.elidhan.anim_guns.item.GunItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.encryption.PlayerPublicKey;
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
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile, PlayerPublicKey publicKey)
    {
        super(world, pos, yaw, profile, publicKey);
    }
    @Inject(method = "getFovMultiplier", at = @At("Tail"), cancellable = true)
    public void zoomLevel(CallbackInfoReturnable<Float> ci){
        ItemStack gun = this.getMainHandStack();

        if(gun.getItem() instanceof GunItem && this.isSneaking() && GunItem.isLoaded(gun))
        {
            NbtCompound nbtCompound = gun.getOrCreateNbt();
            if(nbtCompound.getBoolean("isScoped"))
            {
                ci.setReturnValue(0.2f);
            }
            else{
                ci.setReturnValue(0.8f);
            }
        }
    }
}
