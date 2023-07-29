package net.elidhan.anim_guns;

import net.elidhan.anim_guns.client.render.BulletRenderer;
import net.elidhan.anim_guns.client.render.GunRenderer;
import net.elidhan.anim_guns.item.ModItems;
import net.elidhan.anim_guns.screen.BlueprintScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AnimatedGunsClient implements ClientModInitializer
{
    public static KeyBinding reloadToggle = new KeyBinding("key.anim_guns.reloadtoggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R,"category.anim_guns.binds");
    public static KeyBinding meleeKey = new KeyBinding("key.anim_guns.aimtoggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V,"category.anim_guns.binds");

    @Override
    public void onInitializeClient()
    {
        //Key bind
        KeyBindingHelper.registerKeyBinding(reloadToggle);
        KeyBindingHelper.registerKeyBinding(meleeKey);

        //Recoil Stuff
        ClientPlayNetworking.registerGlobalReceiver(AnimatedGuns.RECOIL_PACKET_ID, (client, handler, buf, sender) ->
        {
            float kick = buf.readFloat();
            double h_kick = buf.readDouble();
            client.execute(() ->
            {
                if(client.player != null)
                {
                    client.player.changeLookDirection(h_kick*5, -kick*5);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AnimatedGuns.GUN_MELEE_PACKET_CLIENT_ID, (client, handler, buf, sender) ->
        {
            client.execute(() ->
            {
                if(client.interactionManager != null && client.player != null && client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY)
                {
                    client.interactionManager.attackEntity(client.player, client.targetedEntity);
                }
            });
        });

        //Entity Render
        EntityRendererRegistry.register(AnimatedGuns.BulletEntityType, BulletRenderer::new);
        HandledScreens.register(AnimatedGuns.BLUEPRINT_SCREEN_HANDLER_TYPE, BlueprintScreen::new);
    }
}
