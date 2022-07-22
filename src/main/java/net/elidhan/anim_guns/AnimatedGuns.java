package net.elidhan.anim_guns;

import net.elidhan.anim_guns.entity.projectile.BulletEntity;
import net.elidhan.anim_guns.item.ModItems;
import net.elidhan.anim_guns.network.PacketHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimatedGuns implements ModInitializer
{
	public static final Identifier RECOIL_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "recoil");
	public static final String MOD_ID = "anim_guns";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EntityType<BulletEntity> BulletEntityType = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(AnimatedGuns.MOD_ID, "bullet"),
			FabricEntityTypeBuilder
					.<BulletEntity>create(SpawnGroup.MISC, BulletEntity::new)
					.dimensions(EntityDimensions.fixed(0.125F, 0.125F))
					.trackRangeBlocks(4)
					.trackedUpdateRate(10)
					.build());
	@Override
	public void onInitialize()
	{
		ModItems.registerModItems();
		PacketHandler.init();
	}
}
