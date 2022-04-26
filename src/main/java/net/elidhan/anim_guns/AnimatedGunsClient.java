package net.elidhan.anim_guns;

import net.elidhan.anim_guns.entity.projectile.BulletEntity;
import net.elidhan.anim_guns.entity.projectile.BulletRenderer;
import net.elidhan.anim_guns.util.ModelPredicateProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AnimatedGunsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelPredicateProvider.registerModels();
        EntityRendererRegistry.register(AnimatedGuns.BulletEntityType, BulletRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(AnimatedGuns.RECOIL_PACKET_ID, (client, handler, buf, sender) -> {
            float kick = buf.readFloat();
            client.execute(() -> {
                if(client.player != null)
                    client.player.setPitch(kick);
            });
        });
    }
}
