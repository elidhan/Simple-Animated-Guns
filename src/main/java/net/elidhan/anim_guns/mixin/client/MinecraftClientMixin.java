package net.elidhan.anim_guns.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.elidhan.anim_guns.item.GunItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    private int itemUseCooldown;
    @Inject(method = "doItemUse", at = @At(value = "RETURN", ordinal = 7))
    public void doItemUse(CallbackInfo ci)
    {
        ItemStack itemStack = this.player.getMainHandStack();
        if (itemStack.getItem() instanceof GunItem)
        {
            itemUseCooldown = 0;
        }
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void gunAim(CallbackInfoReturnable<Boolean> cir)
    {
        if (player != null && player.getMainHandStack().getItem() instanceof GunItem && !player.getMainHandStack().getOrCreateNbt().getBoolean("isReloading"))
        {
            ((GunItem) player.getMainHandStack().getItem()).toggleAim(player.getMainHandStack());
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void handleBlockBreaking(boolean b, CallbackInfo ci)
    {
        if (player != null && player.getMainHandStack().getItem() instanceof GunItem)
        {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult;shouldSwingHand()Z"))
    private boolean noSwingGun(boolean original)
    {
        return original && this.player != null && !(this.player.getMainHandStack().getItem() instanceof GunItem);
    }
}