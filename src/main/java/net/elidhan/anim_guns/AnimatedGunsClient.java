package net.elidhan.anim_guns;

import net.elidhan.anim_guns.entity.projectile.BulletEntityRenderer;
import net.elidhan.anim_guns.util.ModelPredicateProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AnimatedGunsClient implements ClientModInitializer
{
    public static KeyBinding reloadToggle = new KeyBinding("key.anim_guns.reloadtoggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R,"category.anim_guns.binds");
    @Override
    public void onInitializeClient()
    {
        KeyBindingHelper.registerKeyBinding(reloadToggle);

        //Item Models Stuff
        ModelPredicateProvider.registerModels();

        //Projectile render
        EntityRendererRegistry.register(AnimatedGuns.BulletEntityType, BulletEntityRenderer::new);

        //Recoil Stuff
        ClientPlayNetworking.registerGlobalReceiver(AnimatedGuns.RECOIL_PACKET_ID, (client, handler, buf, sender) -> {
            float kick = buf.readFloat();
            client.execute(() -> {
                if(client.player != null)
                    client.player.setPitch(kick);
            });
        });
    }
}
