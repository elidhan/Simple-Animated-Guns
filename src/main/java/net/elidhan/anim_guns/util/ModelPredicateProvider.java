package net.elidhan.anim_guns.util;

import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.item.ModItems;
import net.elidhan.anim_guns.item.GunItem;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class ModelPredicateProvider
{
    public static void registerModels()
    {
        registerGun(ModItems.PISTOL);
        registerGun(ModItems.HEAVY_PISTOL);
        registerGun(ModItems.MAGNUM_REVOLVER);
        registerGun(ModItems.LIGHT_ASSAULT_RIFLE);
        registerGun(ModItems.HEAVY_ASSAULT_RIFLE);
        registerGun(ModItems.COMBAT_SHOTGUN);
        registerGun(ModItems.CLASSIC_SNIPER_RIFLE);
    }
    public static void registerGun(Item gun)
    {
        FabricModelPredicateProviderRegistry.register(gun, new Identifier(AnimatedGuns.MOD_ID, "load_tick"), ((stack, world, entity, seed) ->
                entity != null
                        && stack.getOrCreateNbt().getBoolean("isReloading")
                        ? (float) stack.getOrCreateNbt().getInt("reloadTick") / 200.0f : 0.0f));

        FabricModelPredicateProviderRegistry.register(gun, new Identifier(AnimatedGuns.MOD_ID, "loading"), ((stack, world, entity, seed) ->
                entity != null
                        && stack.getOrCreateNbt().getBoolean("isReloading")
                        ? 1.0f : 0.0f));

        FabricModelPredicateProviderRegistry.register(gun, new Identifier(AnimatedGuns.MOD_ID, "aiming"), (stack, world, entity, seed) ->
                entity != null
                        && entity.isSneaking()
                        && GunItem.isLoaded(stack)
                        ? 1.0f : 0.0f);

        FabricModelPredicateProviderRegistry.register(gun, new Identifier(AnimatedGuns.MOD_ID, "sprinting"), (stack, world, entity, seed) ->
                entity != null
                        && entity.getStackInHand(Hand.MAIN_HAND) == stack
                        && entity.isSprinting()
                        ? 1.0f : 0.0f);
    }
}
