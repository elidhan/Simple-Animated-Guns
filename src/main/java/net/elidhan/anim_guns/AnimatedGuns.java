package net.elidhan.anim_guns;

import net.elidhan.anim_guns.entity.projectile.BulletEntity;
import net.elidhan.anim_guns.item.ModItems;
import net.elidhan.anim_guns.item.GunItem;
import net.elidhan.anim_guns.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimatedGuns implements ModInitializer
{
	public static final Identifier RECOIL_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "recoil");
	public static final String MOD_ID = "anim_guns";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ItemGroup MISC = FabricItemGroupBuilder.build(
			new Identifier(MOD_ID, "misc"),
			() -> new ItemStack(ModItems.HARDENED_IRON_INGOT));
	public static final ItemGroup GUNS = FabricItemGroupBuilder.build(
			new Identifier(MOD_ID, "guns"),
			() -> new ItemStack(ModItems.MAGNUM_REVOLVER));
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
		ModSounds.registerSounds();

		ServerPlayNetworking.registerGlobalReceiver(new Identifier("anim_guns:reload"), (server, player, serverPlayNetworkHandler, buf, packetSender) ->
		{
			if (player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof GunItem)
			{
				player.getStackInHand(Hand.MAIN_HAND).getOrCreateNbt().putBoolean("isReloading", buf.readBoolean());
			}
		});
	}
}
