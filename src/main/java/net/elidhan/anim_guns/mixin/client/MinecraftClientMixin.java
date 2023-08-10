package net.elidhan.anim_guns.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.elidhan.anim_guns.item.GunItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    private int itemUseCooldown;
    @Shadow @Final
    public GameOptions options;
    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult;isAccepted()Z", ordinal = 3),locals = LocalCapture.CAPTURE_FAILHARD)
    public void doItemUse(CallbackInfo ci, Hand[] var1, int var2, int var3, Hand hand, ItemStack itemStack, ActionResult actionResult3)
    {
        if (hand == Hand.MAIN_HAND && itemStack.getItem() instanceof GunItem)
        {
            itemUseCooldown = 0;
        }
    }

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getEnabledFeatures()Lnet/minecraft/resource/featuretoggle/FeatureSet;", shift = At.Shift.BEFORE),locals = LocalCapture.CAPTURE_FAILHARD)
    public void cancelIfGun(CallbackInfo ci, Hand[] var1, int var2, int var3, Hand hand, ItemStack itemStack)
    {
        if (hand == Hand.MAIN_HAND && itemStack.getItem() instanceof GunItem)
        {
            this.crosshairTarget = null;
        }
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void gunAim(CallbackInfoReturnable<Boolean> cir)
    {
        if (player != null && player.getMainHandStack().getItem() instanceof GunItem && !player.getMainHandStack().getOrCreateNbt().getBoolean("isReloading"))
        {
            //I hope I don't break anything
            if(this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.ENTITY && ((EntityHitResult)this.crosshairTarget).getEntity() instanceof ItemFrameEntity entity)
            {
                assert this.world != null;
                if (!this.world.getWorldBorder().contains(entity.getBlockPos())) {
                    return;
                }
                assert this.interactionManager != null;
                ActionResult actionResult = this.interactionManager.interactEntityAtLocation(this.player, entity, (EntityHitResult)this.crosshairTarget, Hand.MAIN_HAND);
                if(!actionResult.isAccepted()) this.interactionManager.interactEntity(this.player, entity, Hand.MAIN_HAND);
                cir.setReturnValue(false);
                return;
            }

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

    @SuppressWarnings("unused")
    @ModifyExpressionValue(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult;shouldSwingHand()Z"))
    private boolean noSwingGun(boolean original)
    {
        return original && this.player != null && !(this.player.getMainHandStack().getItem() instanceof GunItem);
    }

    @SuppressWarnings("unused")
    @ModifyExpressionValue(
            method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 3),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z", ordinal = 0))
    )
    private boolean swapAttackKey(boolean original)
    {
        return (original && !(this.player.getMainHandStack().getItem() instanceof GunItem)) || (this.player.getMainHandStack().getItem() instanceof GunItem && this.options.useKey.wasPressed());
    }

    @SuppressWarnings("unused")
    @ModifyExpressionValue(
            method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 4),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z", ordinal = 0))
    )
    private boolean swapUseKey(boolean original)
    {
        return (original && !(this.player.getMainHandStack().getItem() instanceof GunItem)) || (this.player.getMainHandStack().getItem() instanceof GunItem && this.options.attackKey.wasPressed());
    }

    @SuppressWarnings("unused")
    @ModifyExpressionValue(
            method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 3))
    private boolean swapUseKey2(boolean original)
    {
        return (original && !(this.player.getMainHandStack().getItem() instanceof GunItem)) || (this.player.getMainHandStack().getItem() instanceof GunItem && this.options.attackKey.isPressed());
    }
}