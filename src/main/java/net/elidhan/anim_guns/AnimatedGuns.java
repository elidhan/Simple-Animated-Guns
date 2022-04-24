package net.elidhan.anim_guns;

import net.elidhan.anim_guns.entity.projectile.BulletEntity;
import net.elidhan.anim_guns.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.util.registry.Registry;

public class AnimatedGuns implements ModInitializer
{
	public static final String MOD_ID = "anim_guns";
	public static final EntityType<BulletEntity> BULLET_ENTITY_ENTITY_TYPE =
			Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "bullet"), FabricEntityTypeBuilder.<BulletEntity>create(SpawnGroup.MISC, BulletEntity::new).dimensions(EntityDimensions.fixed(0.125F, 0.125F)).trackRangeBlocks(4).trackedUpdateRate(10).build());
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void onInitialize()
	{
		ModItems.registerModItems();
	}
}
