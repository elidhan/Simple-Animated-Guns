package net.elidhan.anim_guns.mixin.client;

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

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    private int itemUseCooldown;
    @Inject(method = "doItemUse", at = @At("RETURN"))
    public void doItemUse(CallbackInfo ci)
    {
        if (this.player == null)
            return;
        ItemStack itemStack = this.player.getMainHandStack();
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof GunItem)
            itemUseCooldown = 0;
    }
}