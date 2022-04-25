package net.elidhan.anim_guns;

import net.elidhan.anim_guns.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimatedGuns implements ModInitializer
{
	public static final Identifier RECOIL_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "recoil");
	public static final String MOD_ID = "anim_guns";
//	public static final EntityType<BulletEntity> BULLET_ENTITY_ENTITY_TYPE =
//			Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "bullet"), FabricEntityTypeBuilder.<BulletEntity>create(SpawnGroup.MISC, BulletEntity::new).dimensions(EntityDimensions.fixed(0.125F, 0.125F)).trackRangeBlocks(4).trackedUpdateRate(10).build());
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void onInitialize()
	{
		ModItems.registerModItems();

		ServerPlayNetworking.registerGlobalReceiver(AnimatedGuns.RECOIL_PACKET_ID, (server, player, handler, buf, sender) -> {
			float kick = buf.readFloat();
			server.execute(() -> {
				player.setPitch(kick);
			});
		});
	}
}
