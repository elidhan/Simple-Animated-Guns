package net.elidhan.anim_guns;

import com.chocohead.mm.api.ClassTinkerers;
import net.elidhan.anim_guns.client.render.BulletRenderer;
import net.elidhan.anim_guns.client.render.WorldViewGunRenderer;
import net.elidhan.anim_guns.item.GunItem;
import net.elidhan.anim_guns.item.ModItems;
import net.elidhan.anim_guns.screen.BlueprintScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import static net.elidhan.anim_guns.AnimatedGuns.PLAY_ANIMATION_PACKET_ID;

@Environment(EnvType.CLIENT)
public class AnimatedGunsClient implements ClientModInitializer
{
    public static KeyBinding reloadToggle = new KeyBinding("key.anim_guns.reloadtoggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R,"category.anim_guns.binds");
    public static KeyBinding meleeKey = new KeyBinding("key.anim_guns.melee", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V,"category.anim_guns.binds");

    public static final BipedEntityModel.ArmPose HANDGUN_ONEHAND = ClassTinkerers.getEnum(BipedEntityModel.ArmPose.class, "HANDGUN_ONEHAND");
    public static final BipedEntityModel.ArmPose HANDGUN_TWOHAND = ClassTinkerers.getEnum(BipedEntityModel.ArmPose.class, "HANDGUN_TWOHAND");
    public static final BipedEntityModel.ArmPose REVOLVER_FANNING = ClassTinkerers.getEnum(BipedEntityModel.ArmPose.class, "REVOLVER_FANNING");
    public static final BipedEntityModel.ArmPose LONG_GUNS = ClassTinkerers.getEnum(BipedEntityModel.ArmPose.class, "LONG_GUNS");
    public static final BipedEntityModel.ArmPose MINIGUN = ClassTinkerers.getEnum(BipedEntityModel.ArmPose.class, "MINIGUN");

    static void registerGunWorldRenderer(Item gun)
    {
        //model
        Identifier gunId = Registries.ITEM.getId(gun);
        var renderer = new WorldViewGunRenderer(gunId);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(gun, renderer);

        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.addModels((new ModelIdentifier(gunId.withPath(gunId.getPath() + "_wv"), "inventory")));
        });
    }

    @Override
    public void onInitializeClient()
    {
        registerGunWorldRenderer(ModItems.PISTOL);
        registerGunWorldRenderer(ModItems.HEAVY_PISTOL);
        registerGunWorldRenderer(ModItems.SERVICE_PISTOL);
        registerGunWorldRenderer(ModItems.MAGNUM_REVOLVER);
        registerGunWorldRenderer(ModItems.OLD_ARMY_REVOLVER);
        registerGunWorldRenderer(ModItems.MACHINE_PISTOL);
        registerGunWorldRenderer(ModItems.HEAVY_SMG);
        registerGunWorldRenderer(ModItems.RAPID_SMG);
        registerGunWorldRenderer(ModItems.LIGHT_ASSAULT_RIFLE);
        registerGunWorldRenderer(ModItems.HEAVY_ASSAULT_RIFLE);
        registerGunWorldRenderer(ModItems.WAR_TORN_ASSAULT_RIFLE);
        registerGunWorldRenderer(ModItems.DOUBLE_BARRELED_SHOTGUN);
        registerGunWorldRenderer(ModItems.COMBAT_SHOTGUN);
        registerGunWorldRenderer(ModItems.RIOT_SHOTGUN);
        registerGunWorldRenderer(ModItems.CLASSIC_SNIPER_RIFLE);
        registerGunWorldRenderer(ModItems.ARCTIC_SNIPER_RIFLE);
        registerGunWorldRenderer(ModItems.BRUSH_GUN);
        registerGunWorldRenderer(ModItems.MARKSMAN_RIFLE);
        registerGunWorldRenderer(ModItems.MOTHERLAND_MARKSMAN_RIFLE);
        registerGunWorldRenderer(ModItems.LMG);
        registerGunWorldRenderer(ModItems.ANTI_MATERIEL_RIFLE);
        registerGunWorldRenderer(ModItems.MINIGUN);

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
                if (client.player != null && client.interactionManager != null && client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY)
                {
                    client.interactionManager.attackEntity(client.player, ((EntityHitResult)client.crosshairTarget).getEntity());
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(PLAY_ANIMATION_PACKET_ID, (client, handler, buf, sender) ->
        {
            String string = buf.readString();
            long id = buf.readLong();
            client.execute(() ->
            {
                if (client.player.getMainHandStack().getItem() instanceof GunItem item && client.options.getPerspective().isFirstPerson())
                {
                    if(item.getAnimatableInstanceCache().getManagerForId(id).getAnimationControllers().get("controller").getCurrentAnimation().animation().name().equals(string) && !string.equals("idle"))
                    {
                        item.getAnimatableInstanceCache().getManagerForId(id).getAnimationControllers().get("controller").forceAnimationReset();
                    }
                    else
                    {
                        item.getAnimatableInstanceCache().getManagerForId(id).getAnimationControllers().get("controller").tryTriggerAnimation(string);
                    }
                }
            });
        });

        //Entity Render
        EntityRendererRegistry.register(AnimatedGuns.BulletEntityType, BulletRenderer::new);

        HandledScreens.register(AnimatedGuns.BLUEPRINT_SCREEN_HANDLER_TYPE, BlueprintScreen::new);


    }


}
