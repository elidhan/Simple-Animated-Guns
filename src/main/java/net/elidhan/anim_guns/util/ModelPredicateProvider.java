package net.elidhan.anim_guns.util;

import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.item.ModItems;
import net.elidhan.anim_guns.item.gun.GunTemplateItem;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class ModelPredicateProvider {

    public static void registerModels() {
        registerGun(ModItems.PISTOL);
        registerGun(ModItems.REVOLVER);
        registerGun(ModItems.ASSAULT_RIFLE);
    }

    public static void registerGun(Item gun) {
        FabricModelPredicateProviderRegistry.register(gun, new Identifier(AnimatedGuns.MOD_ID, "load_tick"), ((stack, world, entity, seed) ->
                entity != null
                        && entity.isUsingItem()
                        && entity.getActiveItem() == stack
                        && !GunTemplateItem.isLoaded(stack)
                        ? (float) entity.getItemUseTime() / 200.0f : 0.0f));

        FabricModelPredicateProviderRegistry.register(gun, new Identifier(AnimatedGuns.MOD_ID, "loading"), ((stack, world, entity, seed) ->
                entity != null
                        && entity.isUsingItem()
                        && entity.getActiveItem() == stack
                        && !GunTemplateItem.isLoaded(stack)
                        ? 1.0f : 0.0f));

        FabricModelPredicateProviderRegistry.register(gun, new Identifier(AnimatedGuns.MOD_ID, "loaded"), (stack, world, entity, seed) ->
                entity != null
                        && GunTemplateItem.isLoaded(stack)
                        ? 1.0f : 0.0f);

    }
}
