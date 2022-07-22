package net.elidhan.anim_guns.network;

import net.elidhan.anim_guns.item.gun.GunTemplateItem;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class PacketHandler
{
    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("anim_guns:reload"), (server, player, serverPlayNetworkHandler, buf, packetSender) ->
        {
            if (player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof GunTemplateItem)
            {
                player.getStackInHand(Hand.MAIN_HAND).getOrCreateNbt().putBoolean("isReloading", buf.readBoolean());
            }
        });
    }
}
