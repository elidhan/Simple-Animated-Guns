package net.elidhan.anim_guns.item;

import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.item.gun.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
	public static final Item STANDARD_HANDGUN_BULLET = registerItem("standard_handgun_cartridge", new Item(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(16)));
	public static final Item HEAVY_HANDGUN_BULLET = registerItem("heavy_handgun_cartridge", new Item(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(8)));
	public static final Item STANDARD_RIFLE_BULLET = registerItem("standard_rifle_cartridge", new Item(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(32)));
	public static final Item HEAVY_RIFLE_BULLET = registerItem("heavy_rifle_cartridge", new Item(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(8)));
	public static final Item SHOTGUN_SHELL = registerItem("shotgun_shell", new Item(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(8)));
	public static final Item PISTOL = registerItem("pistol", new PistolGunItem(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1)));
	public static final Item REVOLVER = registerItem("revolver", new RevolverItem(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1)));
	public static final Item PUMP_SHOTGUN = registerItem("pump_shotgun", new PumpShotgunItem(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1)));
	public static final Item LIGHT_ASSAULT_RIFLE = registerItem("assaultrifle_light", new LightAssaultRifleItem(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1)));

	private static Item registerItem(String name, Item item)
	{
		return Registry.register(Registry.ITEM, new Identifier(AnimatedGuns.MOD_ID, name), item);
	}
	
	public static void registerModItems()
	{
		AnimatedGuns.LOGGER.info("Registering ModItems for " + AnimatedGuns.MOD_ID);
	}

}
