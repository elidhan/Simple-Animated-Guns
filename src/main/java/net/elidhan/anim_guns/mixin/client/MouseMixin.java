package net.elidhan.anim_guns.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.elidhan.anim_guns.item.GunItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(Mouse.class)
public class MouseMixin
{
    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyExpressionValue(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingSpyglass()Z"))
    public boolean ifScopedGun(boolean original)
    {
        return (original || (this.client.player.getMainHandStack().getItem() instanceof GunItem && ((GunItem)this.client.player.getMainHandStack().getItem()).isScoped() && this.client.player.getMainHandStack().getOrCreateNbt().getBoolean("isAiming")));
    }
}
