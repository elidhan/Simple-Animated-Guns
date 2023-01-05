package net.elidhan.anim_guns.mixin.client;

import net.elidhan.anim_guns.item.GunItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
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
    @Shadow
    @Nullable
    public HitResult crosshairTarget;
    @Shadow
    @Nullable
    public ClientWorld world;
    @Shadow
    protected int attackCooldown;
    @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;
    @Shadow
    @Final
    public GameOptions options;

    @Shadow
    protected abstract void doItemUse();

    @Inject(method = "doItemUse", at = @At("RETURN"))
    public void doItemUse(CallbackInfo ci)
    {
        if (this.player == null)
            return;
        ItemStack itemStack = this.player.getMainHandStack();
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof GunItem)
            itemUseCooldown = 0;
    }

    //Replace attack animation
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void gunMeleeAttack(CallbackInfoReturnable<Boolean> cir)
    {
        if (player != null && player.getMainHandStack().getItem() instanceof GunItem)
        {
            ((GunItem)player.getMainHandStack().getItem()).doMeleeAttack(player.getMainHandStack());
            if (this.attackCooldown > 0) {
                cir.setReturnValue(false);
            }
            if (this.crosshairTarget == null) {
                if (this.interactionManager.hasLimitedAttackSpeed()) {
                    this.attackCooldown = 10;
                }
                cir.setReturnValue(false);
            }
            if (this.player.isRiding()) {
                cir.setReturnValue(false);
            }
            switch (this.crosshairTarget.getType())
            {
                case ENTITY -> this.interactionManager.attackEntity(this.player, ((EntityHitResult) this.crosshairTarget).getEntity());
                case MISS ->
                {
                    {
                        if (this.interactionManager.hasLimitedAttackSpeed())
                        {
                            this.attackCooldown = 10;
                        }
                        this.player.resetLastAttackedTicks();
                    }
                }
            }
            cir.setReturnValue(false);
        }
    }

    //Prevent mining animation when holding gun
    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void handleBlockBreaking(boolean b, CallbackInfo ci)
    {
        if (player != null && player.getMainHandStack().getItem() instanceof GunItem)
        {
            ci.cancel();
        }
    }
}