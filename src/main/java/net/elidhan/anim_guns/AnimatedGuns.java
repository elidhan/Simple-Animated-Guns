package net.elidhan.anim_guns;

import net.elidhan.anim_guns.item.BlueprintBundleItem;
import net.elidhan.anim_guns.item.BlueprintItem;
import net.elidhan.anim_guns.item.ModItems;
import net.elidhan.anim_guns.item.GunItem;
import net.elidhan.anim_guns.screen.BlueprintScreenHandler;
import net.elidhan.anim_guns.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class AnimatedGuns implements ModInitializer
{
	public static final String MOD_ID = "anim_guns";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier RECOIL_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "recoil");
	public static final Identifier RELOAD_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "reload");
	public static final Identifier SELECT_BLUEPRINT_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "select_blueprint");
	public static final Identifier GUN_MELEE_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "gun_melee");
	public static final Identifier GUN_AIM_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "aim");
	public static final Identifier GUN_SPRINT_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "sprint");

	public static final ItemGroup MISC = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "misc"), () -> new ItemStack(ModItems.MAGNUM_REVOLVER_BLUEPRINT));
	public static final ItemGroup GUNS = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "guns"), () -> new ItemStack(ModItems.MAGNUM_REVOLVER));
	public static final ScreenHandlerType<BlueprintScreenHandler> BLUEPRINT_SCREEN_HANDLER_TYPE = Registry.register(Registry.SCREEN_HANDLER, new Identifier(MOD_ID, "blueprint_screenhandler"), new ScreenHandlerType<>(BlueprintScreenHandler::new));

	@Override
	public void onInitialize()
	{
		ModItems.registerModItems();
		ModSounds.registerSounds();

		ServerPlayNetworking.registerGlobalReceiver(RELOAD_PACKET_ID, (server, player, serverPlayNetworkHandler, buf, packetSender) ->
		{
			if (player.getMainHandStack().getItem() instanceof GunItem)
			{
				ItemStack stack = player.getMainHandStack();
				player.setSprinting(false);
				stack.getOrCreateNbt().putBoolean("isReloading", buf.readBoolean());

				final int id = GeckoLibUtil.guaranteeIDForStack(stack, player.getWorld());
				GeckoLibNetwork.syncAnimation(player, (GunItem)stack.getItem(), id, stack.getOrCreateNbt().getBoolean("isAiming")?8:2);
				for (PlayerEntity otherPlayer : PlayerLookup.tracking(player))
				{
					GeckoLibNetwork.syncAnimation(otherPlayer, (GunItem)stack.getItem(), id, stack.getOrCreateNbt().getBoolean("isAiming")?8:2);
				}
				stack.getOrCreateNbt().putBoolean("isAiming", false);
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(SELECT_BLUEPRINT_PACKET_ID, (server, player, serverPlayNetworkHandler, buf, packetSender) ->
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
		ServerPlayNetworking.registerGlobalReceiver(GUN_MELEE_PACKET_ID, (server, player, serverPlayNetworkHandler, buf, packetSender) ->
		{
			ItemStack stack = buf.readItemStack();

			((GunItem)stack.getItem()).toggleAim(player.getMainHandStack(),false, player.getWorld(), player);

			float i = player.getItemCooldownManager().getCooldownProgress(stack.getItem(), 0)*((GunItem) stack.getItem()).getRateOfFire();
			if ((int)i < 4) player.getItemCooldownManager().set(stack.getItem(), 10);

			final int id = GeckoLibUtil.guaranteeIDForStack(stack, player.getWorld());
			GeckoLibNetwork.syncAnimation(player, (GunItem)stack.getItem(), id, 9);
			for (PlayerEntity otherPlayer : PlayerLookup.tracking(player))
			{
				GeckoLibNetwork.syncAnimation(otherPlayer, (GunItem)stack.getItem(), id, 9);
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(GUN_AIM_PACKET_ID, (server, player, serverPlayNetworkHandler, buf, packetSender) ->
		{
			if (player.getMainHandStack().getItem() instanceof GunItem)
			{
				((GunItem) player.getMainHandStack().getItem()).toggleAim(player.getMainHandStack(),buf.readBoolean(), player.getWorld(), player);
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(GUN_SPRINT_PACKET_ID, (server, player, serverPlayNetworkHandler, buf, packetSender) ->
		{
			ItemStack stack = buf.readItemStack();

			if (stack.getItem() instanceof GunItem)
			{
				((GunItem)stack.getItem()).toggleSprint(stack,buf.readBoolean(), player.getWorld(), player);
			}
		});
	}
}
