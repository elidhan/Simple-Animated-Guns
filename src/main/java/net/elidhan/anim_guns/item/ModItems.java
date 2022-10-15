package net.elidhan.anim_guns.item;

import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.sound.ModSounds;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
	public static final Item HARDENED_IRON_INGOT = registerItem("hardened_iron_ingot", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
    public static final Item HARDENED_IRON_NUGGET = registerItem("hardened_iron_nugget", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item PLASTIC = registerItem("plastic", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));

	public static final Item PISTOL_GRIP = registerItem("pistol_grip", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item GUN_SCOPE = registerItem("gun_scope", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));

	public static final Item LONG_BARREL = registerItem("long_barrel", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item SHORT_BARREL = registerItem("short_barrel", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item WOODEN_STOCK = registerItem("wooden_stock", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item MODERN_STOCK = registerItem("modern_stock", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item WOODEN_HANDGUARD = registerItem("wooden_handguard", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item MODERN_HANDGUARD = registerItem("modern_handguard", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));

	public static final Item BLUEPRINT_BUNDLE = registerItem("blueprint_bundle", new BlueprintBundleItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));

	public static final Item PISTOL_BLUEPRINT = registerItem("blueprint_pistol_light", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item MAGNUM_REVOLVER_BLUEPRINT = registerItem("blueprint_revolver_magnum", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item LIGHT_ASSAULT_RIFLE_BLUEPRINT = registerItem("blueprint_assaultrifle_light", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item HEAVY_ASSAULT_RIFLE_BLUEPRINT = registerItem("blueprint_assaultrifle_heavy", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item COMBAT_SHOTGUN_BLUEPRINT = registerItem("blueprint_shotgun_combat", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item CLASSIC_SNIPER_RIFLE_BLUEPRINT = registerItem("blueprint_sniper_classic", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));

	public static final Item STANDARD_HANDGUN_BULLET = registerItem("standard_handgun_cartridge", new Item(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(64)));
	public static final Item HEAVY_HANDGUN_BULLET = registerItem("heavy_handgun_cartridge", new Item(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(64)));
	public static final Item STANDARD_RIFLE_BULLET = registerItem("standard_rifle_cartridge", new Item(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(64)));
	public static final Item HEAVY_RIFLE_BULLET = registerItem("heavy_rifle_cartridge", new Item(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(64)));
	public static final Item SHOTGUN_SHELL = registerItem("shotgun_shell", new Item(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(64)));
	public static final Item PISTOL = registerItem("pistol_light", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
			5.5f,
			4,
			17,
			STANDARD_HANDGUN_BULLET,
			26,
			0.25f,
			2.5f,
			1,
			1,
			ModSounds.RELOAD_GENERIC_PISTOL_P1,
			ModSounds.RELOAD_GENERIC_PISTOL_P2,
			ModSounds.RELOAD_GENERIC_PISTOL_P3,
			ModSounds.PISTOL_LIGHT,
			1,
			false,
			6,
			16,
			20)
	{
	});
	public static final Item HEAVY_PISTOL = registerItem("pistol_heavy", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
			11,
			5,
			7,
			HEAVY_HANDGUN_BULLET,
			26,
			0.25f,
			7.5f,
			1,
			1,
			ModSounds.RELOAD_GENERIC_PISTOL_P1,
			ModSounds.RELOAD_GENERIC_PISTOL_P2,
			ModSounds.RELOAD_GENERIC_PISTOL_P3,
			ModSounds.PISTOL_HEAVY,
			1,
			false,
			6,
			16,
			20)
	{
	});
    public static final Item MAGNUM_REVOLVER = registerItem("revolver_magnum", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
            11,
            10,
            6,
            HEAVY_HANDGUN_BULLET,
            40,
            0.125f,
            6.5f,
            1,
            1,
            ModSounds.RELOAD_GENERIC_REVOLVER_P1,
            ModSounds.RELOAD_GENERIC_REVOLVER_P2,
            ModSounds.RELOAD_GENERIC_REVOLVER_P3,
            ModSounds.REVOLVER_MAGNUM,
            1,
            false,
            1,
            26,
            34)
    {
    });
	public static final Item MACHINE_PISTOL = registerItem("smg_machinepistol", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
			5,
			1,
			30,
			STANDARD_HANDGUN_BULLET,
			44,
			5,
			2,
			1,
			1,
			ModSounds.RELOAD_GENERIC_SMG_P1,
			ModSounds.RELOAD_GENERIC_SMG_P2,
			ModSounds.RELOAD_GENERIC_SMG_P3,
			ModSounds.SMG_MACHINEPISTOL,
			1,
			false,
			6,
			18,
			37)
	{
	});
	public static final Item LIGHT_ASSAULT_RIFLE = registerItem("assaultrifle_light", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
		5.5f,
		2,
		30,
		STANDARD_RIFLE_BULLET,
		44,
		0.15f,
		1.625f,
		1,
		1,
		ModSounds.RELOAD_GENERIC_AR_P1,
		ModSounds.RELOAD_GENERIC_AR_P2,
		ModSounds.RELOAD_GENERIC_AR_P3,
		ModSounds.ASSAULTRIFLE_LIGHT,
		1,
		false,
		6,
		18,
		37)
	{
	});
	public static final Item HEAVY_ASSAULT_RIFLE = registerItem("assaultrifle_heavy", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
		8,
		3,
		20,
		STANDARD_RIFLE_BULLET,
		48,
		0.125f,
		2.5f,
		1,
		1,
		ModSounds.RELOAD_HEAVY_AR_P1,
		ModSounds.RELOAD_HEAVY_AR_P2,
		ModSounds.RELOAD_HEAVY_AR_P3,
		ModSounds.ASSAULTRIFLE_HEAVY,
		1,
		false,
		6,
		22,
		40)
	{
	});
	public static final Item COMBAT_SHOTGUN = registerItem("shotgun_combat", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
        5.5f,
        14,
        6,
        SHOTGUN_SHELL,
        26,
        9.25f,
        8.25f,
        5,
        2,
        ModSounds.RELOAD_COMBAT_SHOTGUN_P1,
        ModSounds.RELOAD_COMBAT_SHOTGUN_P2,
        ModSounds.RELOAD_COMBAT_SHOTGUN_P3,
        ModSounds.SHOTGUN_COMBAT,
        6,
        false,
        1,
        4,
        13)
	{
	});
	public static final Item CLASSIC_SNIPER_RIFLE = registerItem("sniper_classic", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
        22,
        20,
        5,
        HEAVY_RIFLE_BULLET,
        26,
        0.01f,
        8.25f,
        1,
        2,
        ModSounds.RELOAD_GENERIC_SNIPER_P1,
        ModSounds.RELOAD_CLASSIC_SNIPER_P2,
        ModSounds.RELOAD_GENERIC_SNIPER_P3,
        ModSounds.SNIPER_CLASSIC,
        5,
        true,
        1,
        8,
        17)
	{
	});

	private static Item registerItem(String name, Item item)
	{
		return Registry.register(Registry.ITEM, new Identifier(AnimatedGuns.MOD_ID, name), item);
	}
	
	public static void registerModItems()
	{
		AnimatedGuns.LOGGER.info("Registering ModItems for " + AnimatedGuns.MOD_ID);
	}

}
