package net.elidhan.anim_guns.mixin.client;

import net.elidhan.anim_guns.item.GunItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    @Shadow
    @Final
    private MinecraftClient client;

    @Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    private void bobViewGun(GameRenderer instance, MatrixStack matrices, float tickDelta)
    {
        if(this.client.player == null)
            return;

       if(!(this.client.player.getMainHandStack().getItem() instanceof GunItem))
       {
           allowBobView(matrices, tickDelta, 1.0f);
       }
       else if (!this.client.player.getMainHandStack().getOrCreateNbt().getBoolean("isAiming") && !this.client.player.isSprinting())
       {
           allowBobView(matrices, tickDelta, 0.25f);
       }
    }

    private void allowBobView(MatrixStack matrices, float tickDelta, float multiplier) {
        if (!(this.client.getCameraEntity() instanceof PlayerEntity playerEntity)) {
            return;
        }
        float f = (playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed);
        float g = -(playerEntity.horizontalSpeed + f * tickDelta);
        float h = MathHelper.lerp(tickDelta, playerEntity.prevStrideDistance, playerEntity.strideDistance) * multiplier;

        matrices.translate(MathHelper.sin(g * (float)Math.PI) * h * 0.5f, -Math.abs(MathHelper.cos(g * (float)Math.PI) * h), 0.0);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.sin(g * (float)Math.PI) * h * 3.0f));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(Math.abs(MathHelper.cos(g * (float)Math.PI - 0.2f) * h) * 5.0f));
    }
}
