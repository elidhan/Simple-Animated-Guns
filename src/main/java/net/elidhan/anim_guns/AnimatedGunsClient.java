package net.elidhan.anim_guns;

import net.elidhan.anim_guns.client.render.GunRenderer;
import net.elidhan.anim_guns.entity.projectile.BulletEntityRenderer;
import net.elidhan.anim_guns.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

@Environment(EnvType.CLIENT)
public class AnimatedGunsClient implements ClientModInitializer
{
    public static KeyBinding reloadToggle = new KeyBinding("key.anim_guns.reloadtoggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R,"category.anim_guns.binds");
    public static KeyBinding aimToggle = new KeyBinding("key.anim_guns.aimtoggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V,"category.anim_guns.binds");

    @Override
    public void onInitializeClient()
    {
        //Key bind
        KeyBindingHelper.registerKeyBinding(reloadToggle);
        KeyBindingHelper.registerKeyBinding(aimToggle);

        //Projectile render
        EntityRendererRegistry.register(AnimatedGuns.BulletEntityType, BulletEntityRenderer::new);

        //Recoil Stuff
        ClientPlayNetworking.registerGlobalReceiver(AnimatedGuns.RECOIL_PACKET_ID, (client, handler, buf, sender) ->
        {
            float v_kick = buf.readFloat();
            float h_kick = (float)buf.readDouble();
            client.execute(() ->
            {
                if(client.player != null)
                {
                    client.player.setPitch(v_kick);
                    client.player.setYaw((h_kick));
                }
            });
        });

        //Geckolib Stuff
        GeoItemRenderer.registerItemRenderer(ModItems.PISTOL, new GunRenderer());
        GeoItemRenderer.registerItemRenderer(ModItems.HEAVY_PISTOL, new GunRenderer());
        GeoItemRenderer.registerItemRenderer(ModItems.MAGNUM_REVOLVER, new GunRenderer());
        GeoItemRenderer.registerItemRenderer(ModItems.OLD_ARMY_REVOLVER, new GunRenderer());
        GeoItemRenderer.registerItemRenderer(ModItems.MACHINE_PISTOL, new GunRenderer());
        GeoItemRenderer.registerItemRenderer(ModItems.LIGHT_ASSAULT_RIFLE, new GunRenderer());
        GeoItemRenderer.registerItemRenderer(ModItems.HEAVY_ASSAULT_RIFLE, new GunRenderer());
        GeoItemRenderer.registerItemRenderer(ModItems.WAR_TORN_ASSAULT_RIFLE, new GunRenderer());
        GeoItemRenderer.registerItemRenderer(ModItems.DOUBLE_BARRELED_SHOTGUN, new GunRenderer());
        GeoItemRenderer.registerItemRenderer(ModItems.COMBAT_SHOTGUN, new GunRenderer());
        GeoItemRenderer.registerItemRenderer(ModItems.CLASSIC_SNIPER_RIFLE, new GunRenderer());
        GeoItemRenderer.registerItemRenderer(ModItems.BRUSH_GUN, new GunRenderer());
    }
}
