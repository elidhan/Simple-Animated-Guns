package net.elidhan.anim_guns.mixin.client;

import com.mojang.authlib.GameProfile;
import net.elidhan.anim_guns.item.GunItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity
{
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey)
    {
        super(world, profile, publicKey);
    }

    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    public void setSprinting(CallbackInfo ci)
    {
        if(this.getMainHandStack().getItem() instanceof GunItem)
        {
            NbtCompound nbtCompound = this.getMainHandStack().getOrCreateNbt();

            if(nbtCompound.getBoolean("isAiming"))
            {
                super.setSprinting(false);
                ci.cancel();
            }
        }
    }
}
