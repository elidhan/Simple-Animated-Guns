package net.elidhan.anim_guns.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.AnimatedGunsClient;
import net.elidhan.anim_guns.item.GunItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
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

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    public void doItemUse(CallbackInfo ci, @Local Hand hand, @Local ItemStack itemStack)
    {
        if (hand == Hand.MAIN_HAND && itemStack.getItem() instanceof GunItem)
        {
            itemUseCooldown = 0;
            if((((GunItem) itemStack.getItem()).getFiringType() == GunItem.FiringType.SEMI_AUTO)) this.options.attackKey.setPressed(false);
        }
        else if (hand == Hand.OFF_HAND && !(itemStack.getItem() instanceof GunItem) && this.player.getMainHandStack().getItem() instanceof GunItem)
        {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"))
    public HitResult.Type cancelIfGun(HitResult.Type original)
    {
        return (this.player.getMainHandStack().getItem() instanceof GunItem) ? HitResult.Type.MISS : original;
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void gunAim(CallbackInfoReturnable<Boolean> cir)
    {
        if (this.player != null && this.player.getMainHandStack().getItem() instanceof GunItem)
        {
            if (!this.player.getMainHandStack().getOrCreateNbt().getBoolean("isReloading"))
            {
                //I want to find a more elegant solution to this
                if (this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.ENTITY && ((EntityHitResult) this.crosshairTarget).getEntity() instanceof ItemFrameEntity entity)
                {
                    if (!this.world.getWorldBorder().contains(entity.getBlockPos()))
                    {
                        return;
                    }
                    ActionResult actionResult = this.interactionManager.interactEntityAtLocation(this.player, entity, (EntityHitResult) this.crosshairTarget, Hand.MAIN_HAND);
                    if (!actionResult.isAccepted())
                        this.interactionManager.interactEntity(this.player, entity, Hand.MAIN_HAND);
                    itemUseCooldown = 4;
                    cir.setReturnValue(false);
                    return;
                }

                this.player.setSprinting(false);

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(!this.player.getMainHandStack().getOrCreateNbt().getBoolean("isAiming"));
                ClientPlayNetworking.send(AnimatedGuns.GUN_AIM_PACKET_ID, buf);
            }

            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V", shift = At.Shift.BEFORE)
    )
    private void meleeKey(CallbackInfo ci)
    {
        while (AnimatedGunsClient.meleeKey.wasPressed())
        {
            if(this.player != null && this.player.getMainHandStack().getItem() instanceof GunItem)
            {
                ClientPlayNetworking.send(AnimatedGuns.GUN_MELEE_PACKET_SERVER_ID, PacketByteBufs.empty());
                if(this.interactionManager != null && this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.ENTITY) this.interactionManager.attackEntity(this.player, ((EntityHitResult)this.crosshairTarget).getEntity());
            }
        }
    }

    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void handleBlockBreaking(boolean b, CallbackInfo ci)
    {
        if (player.getMainHandStack().getItem() instanceof GunItem)
        {
            ci.cancel();
        }
    }

    @SuppressWarnings("unused")
    @ModifyExpressionValue(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult;shouldSwingHand()Z"))
    private boolean dontSwingGun(boolean original)
    {
        return original && !(this.player.getMainHandStack().getItem() instanceof GunItem);
    }

    @SuppressWarnings("unused")
    @ModifyExpressionValue(
            method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 3),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z", ordinal = 0))
    )
    private boolean swapAttackKey(boolean original)
    {
        return (original && !(this.player.getMainHandStack().getItem() instanceof GunItem)) || (this.player.getMainHandStack().getItem() instanceof GunItem && (this.options.useKey.wasPressed()));
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