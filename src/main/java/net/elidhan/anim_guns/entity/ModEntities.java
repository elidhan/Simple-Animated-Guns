package net.elidhan.anim_guns.entity;

import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.entity.projectile.BulletProjectileEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities
{
    public static final EntityType<BulletProjectileEntity> BULLET = Registry.register(Registry.ENTITY_TYPE, new Identifier(AnimatedGuns.MOD_ID, "bullet"), FabricEntityTypeBuilder.<BulletProjectileEntity>create(SpawnGroup.MISC, BulletProjectileEntity::new).dimensions(EntityDimensions.fixed(0.0625f, 0.0625f)).build());
}
