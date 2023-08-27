package net.elidhan.anim_guns.mixin.client;

import net.elidhan.anim_guns.item.GunItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRenderMixin
{
    @Inject(method = "getArmPose", at = @At("TAIL"), cancellable = true)
    private static void gunPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> ci)
    {
        if(hand == Hand.MAIN_HAND && player.getStackInHand(hand).getItem() instanceof GunItem)
        {
            if(player.getStackInHand(hand).getOrCreateNbt().getInt("reloadTick") > 0)
            {
                ci.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_CHARGE);
                return;
            }

            if (GunItem.isLoaded(player.getStackInHand(hand)) && !player.isSprinting()) ci.setReturnValue(BipedEntityModel.ArmPose.BOW_AND_ARROW);
        }
    }
}
