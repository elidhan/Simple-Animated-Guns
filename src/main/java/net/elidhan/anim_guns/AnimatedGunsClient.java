package net.elidhan.anim_guns;

import net.elidhan.anim_guns.entity.projectile.BulletRenderer;
import net.elidhan.anim_guns.util.ModelPredicateProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AnimatedGunsClient implements ClientModInitializer {
    public static KeyBinding reloadToggle = new KeyBinding("key.anim_guns.reloadtoggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R,"category.anim_guns.binds");
    @Override
    public void onInitializeClient() {
        //Model Predicates
        ModelPredicateProvider.registerModels();

        //Entity rendering
        EntityRendererRegistry.register(AnimatedGuns.BulletEntityType, (ctx) -> new BulletRenderer(ctx));

        //Packet stuff
        ClientPlayNetworking.registerGlobalReceiver(AnimatedGuns.RECOIL_PACKET_ID, (client, handler, buf, sender) -> {
            float kick = buf.readFloat();
            client.execute(() -> {
                if(client.player != null)
                    client.player.setPitch(kick);
            });
        });
    }
}
