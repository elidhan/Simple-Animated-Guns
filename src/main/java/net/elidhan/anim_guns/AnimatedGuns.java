package net.elidhan.anim_guns;

import net.elidhan.anim_guns.entity.projectile.BulletEntity;
import net.elidhan.anim_guns.item.BlueprintBundleItem;
import net.elidhan.anim_guns.item.BlueprintItem;
import net.elidhan.anim_guns.item.ModItems;
import net.elidhan.anim_guns.item.GunItem;
import net.elidhan.anim_guns.screen.BlueprintScreenHandler;
import net.elidhan.anim_guns.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimatedGuns implements ModInitializer
{
	public static final String MOD_ID = "anim_guns";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier RECOIL_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "recoil");
	public static final ItemGroup MISC = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "misc"), () -> new ItemStack(ModItems.MAGNUM_REVOLVER_BLUEPRINT));
	public static final ItemGroup GUNS = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "guns"), () -> new ItemStack(ModItems.MAGNUM_REVOLVER));

	public static final EntityType<BulletEntity> BulletEntityType = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(MOD_ID, "bullet"),
			FabricEntityTypeBuilder.<BulletEntity>create(SpawnGroup.MISC, BulletEntity::new).dimensions(EntityDimensions.fixed(0.0625f,0.0625f)).trackRangeBlocks(4).trackedUpdateRate(10).build());

	public static final ScreenHandlerType<BlueprintScreenHandler> BLUEPRINT_SCREEN_HANDLER_TYPE =
			Registry.register(Registry.SCREEN_HANDLER, new Identifier(MOD_ID, "blueprint_screenhandler"), new ScreenHandlerType<>(BlueprintScreenHandler::new));

	@Override
	public void onInitialize()
	{
		ModItems.registerModItems();
		ModSounds.registerSounds();

		ServerPlayNetworking.registerGlobalReceiver(new Identifier(MOD_ID,"reload"), (server, player, serverPlayNetworkHandler, buf, packetSender) ->
		{
			if (player.getMainHandStack().getItem() instanceof GunItem)
			{
				ItemStack stack = player.getMainHandStack();

				stack.getOrCreateNbt().putBoolean("isReloading", buf.readBoolean());
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(new Identifier(MOD_ID,"select_blueprint"), (server, player, serverPlayNetworkHandler, buf, packetSender) ->
		{
			int i = buf.readInt();
			Item blueprint = BlueprintItem.BLUEPRINT_ITEM_LIST.get(i);

			if(player.getMainHandStack().getItem() instanceof BlueprintItem || player.getMainHandStack().getItem() instanceof BlueprintBundleItem)
			{
				player.getMainHandStack().decrement(1);
			}
			else if(player.getOffHandStack().getItem() instanceof BlueprintItem || player.getOffHandStack().getItem() instanceof BlueprintBundleItem)
			{
				player.getOffHandStack().decrement(1);
			}

			if(player.getInventory().getEmptySlot() > -1)
			{
				player.giveItemStack(new ItemStack(blueprint));
			}
			else
			{
				player.dropItem(new ItemStack(blueprint), false, true);
			}

		});

		ServerPlayNetworking.registerGlobalReceiver(new Identifier(MOD_ID,"aim"), (server, player, serverPlayNetworkHandler, buf, packetSender) ->
		{
			if (player.getMainHandStack().getItem() instanceof GunItem)
			{
				((GunItem) player.getMainHandStack().getItem()).toggleAim(player.getMainHandStack(),buf.readBoolean(), player.getWorld(), player);
			}
		});
	}
}
