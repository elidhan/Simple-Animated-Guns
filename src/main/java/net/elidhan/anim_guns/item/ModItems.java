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
	public static final Item PISTOL_MAGAZINE = registerItem("pistol_magazine", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item REVOLVER_CHAMBER = registerItem("revolver_chamber", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item SMG_MAGAZINE = registerItem("smg_magazine", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item RIFLE_MAGAZINE = registerItem("rifle_magazine", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));
	public static final Item TUBE_MAGAZINE = registerItem("tube_magazine", new Item(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(64)));

	public static final Item BLUEPRINT_BUNDLE = registerItem("blueprint_bundle", new BlueprintBundleItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));

	public static final Item PISTOL_BLUEPRINT = registerItem("blueprint_pistol_light", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));
	public static final Item HEAVY_PISTOL_BLUEPRINT = registerItem("blueprint_pistol_heavy", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));
	public static final Item MAGNUM_REVOLVER_BLUEPRINT = registerItem("blueprint_revolver_magnum", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));
	public static final Item OLD_ARMY_REVOLVER_BLUEPRINT = registerItem("blueprint_revolver_coltarmy", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));
	public static final Item MACHINE_PISTOL_BLUEPRINT = registerItem("blueprint_smg_machinepistol", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));
	public static final Item HEAVY_SMG_BLUEPRINT = registerItem("blueprint_smg_heavy", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));
	public static final Item LIGHT_ASSAULT_RIFLE_BLUEPRINT = registerItem("blueprint_assaultrifle_light", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));
	public static final Item HEAVY_ASSAULT_RIFLE_BLUEPRINT = registerItem("blueprint_assaultrifle_heavy", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));
	public static final Item WAR_TORN_ASSAULT_RIFLE_BLUEPRINT = registerItem("blueprint_assaultrifle_rus", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));
	public static final Item COMBAT_SHOTGUN_BLUEPRINT = registerItem("blueprint_shotgun_combat", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));
	public static final Item RIOT_SHOTGUN_BLUEPRINT = registerItem("blueprint_shotgun_riot", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));
	public static final Item DOUBLE_BARRELED_SHOTGUN_BLUEPRINT = registerItem("blueprint_shotgun_doublebarrel", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));
	public static final Item CLASSIC_SNIPER_RIFLE_BLUEPRINT = registerItem("blueprint_sniper_classic", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));
	public static final Item BRUSH_GUN_BLUEPRINT = registerItem("blueprint_sniper_cowboy", new BlueprintItem(new FabricItemSettings().group(AnimatedGuns.MISC).maxCount(1)));

	public static final Item STANDARD_HANDGUN_BULLET = registerItem("standard_handgun_cartridge", new Item(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(64)));
	public static final Item HEAVY_HANDGUN_BULLET = registerItem("heavy_handgun_cartridge", new Item(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(64)));
	public static final Item STANDARD_RIFLE_BULLET = registerItem("standard_rifle_cartridge", new Item(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(64)));
	public static final Item HEAVY_RIFLE_BULLET = registerItem("heavy_rifle_cartridge", new Item(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(64)));
	public static final Item SHOTGUN_SHELL = registerItem("shotgun_shell", new Item(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(64)));

	public static final Item PISTOL = registerItem("pistol_light", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
			"pistol_light",
			"pistol_generic",
			5f,
			4,
			17,
			STANDARD_HANDGUN_BULLET,
			35,
			1.5f,
			new float[] {1f, 2.5f},
			1,
			GunItem.LoadingType.MAGAZINE,
			null,
			ModSounds.RELOAD_GENERIC_PISTOL_P1,
			ModSounds.RELOAD_GENERIC_PISTOL_P2,
			ModSounds.RELOAD_GENERIC_PISTOL_P3,
			ModSounds.PISTOL_LIGHT,
			1,
			false,
			10,
			11,
			21)
	{
	});
	public static final Item HEAVY_PISTOL = registerItem("pistol_heavy", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
			"pistol_heavy",
			"pistol_heavy",
			11f,
			5,
			7,
			HEAVY_HANDGUN_BULLET,
			35,
			1.5f,
			new float[] {3f, 8.5f},
			1,
			GunItem.LoadingType.MAGAZINE,
			null,
			ModSounds.RELOAD_GENERIC_PISTOL_P1,
			ModSounds.RELOAD_GENERIC_PISTOL_P2,
			ModSounds.RELOAD_GENERIC_PISTOL_P3,
			ModSounds.PISTOL_HEAVY,
			1,
			false,
			10,
			11,
			21)
	{
	});
    public static final Item MAGNUM_REVOLVER = registerItem("revolver_magnum", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
			"revolver_magnum",
			"revolver_generic",
			11f,
            10,
            6,
            HEAVY_HANDGUN_BULLET,
            44,
            0.125f,
			new float[] {2f, 6f},
            1,
			GunItem.LoadingType.MAGAZINE,
			ModSounds.RELOAD_GENERIC_REVOLVER_P0,
			ModSounds.RELOAD_GENERIC_REVOLVER_P1,
			ModSounds.RELOAD_GENERIC_REVOLVER_P2,
			ModSounds.RELOAD_GENERIC_REVOLVER_P3,
            ModSounds.REVOLVER_MAGNUM,
            1,
            false,
            10,
            20,
            30)
    {
    });
    public static final Item OLD_ARMY_REVOLVER = registerItem("revolver_coltarmy", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
            "revolver_coltarmy",
            "revolver_coltarmy",
            22,
            20,
            6,
            HEAVY_HANDGUN_BULLET,
            70,
            0.125f,
			new float[] {2f, 8.5f},
            1,
			GunItem.LoadingType.PER_CARTRIDGE,
			null,
            ModSounds.RELOAD_OLD_ARMY_REVOLVER_P1,
            ModSounds.RELOAD_OLD_ARMY_REVOLVER_P2,
            ModSounds.RELOAD_OLD_ARMY_REVOLVER_P3,
            ModSounds.REVOLVER_COLTARMY,
            6,
            false,
            20,
            21,
            50)
    {
    });
	public static final Item MACHINE_PISTOL = registerItem("smg_machinepistol", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
			"smg_machinepistol",
			"smg_machinepistol",
			5f,
			1,
			30,
			STANDARD_HANDGUN_BULLET,
			35,
			2.5f,
			new float[] {2.5f, 1.5f},
			1,
			GunItem.LoadingType.MAGAZINE,
			null,
			ModSounds.RELOAD_GENERIC_SMG_P1,
			ModSounds.RELOAD_GENERIC_SMG_P2,
			ModSounds.RELOAD_GENERIC_SMG_P3,
			ModSounds.SMG_MACHINEPISTOL,
			1,
			false,
			10,
			11,
			21)
	{
	});
    public static final Item HEAVY_SMG = registerItem("smg_heavy", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
            "smg_heavy",
            "smg_heavy",
            5.5f,
            2,
            25,
            STANDARD_HANDGUN_BULLET,
            60,
            2.5f,
            new float[] {1.5f, 2f},
            1,
			GunItem.LoadingType.MAGAZINE,
			ModSounds.RELOAD_HEAVY_SMG_P0,
            ModSounds.RELOAD_HEAVY_SMG_P1,
            ModSounds.RELOAD_HEAVY_SMG_P2,
            ModSounds.RELOAD_HEAVY_SMG_P3,
            ModSounds.SMG_HEAVY,
            1,
            false,
            20,
            27,
            49)
    {
    });
	public static final Item LIGHT_ASSAULT_RIFLE = registerItem("assaultrifle_light", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
		"assaultrifle_light",
		"assaultrifle_generic",
		6f,
		2,
		30,
		STANDARD_RIFLE_BULLET,
		50,
		0.5f,
		new float[] {1f, 2f},
		1,
		GunItem.LoadingType.MAGAZINE,
		null,
		ModSounds.RELOAD_GENERIC_AR_P1,
		ModSounds.RELOAD_GENERIC_AR_P2,
		ModSounds.RELOAD_GENERIC_AR_P3,
		ModSounds.ASSAULTRIFLE_LIGHT,
		1,
		false,
		5,
		21,
		32)
	{
	});
	public static final Item HEAVY_ASSAULT_RIFLE = registerItem("assaultrifle_heavy", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
		"assaultrifle_heavy",
		"assaultrifle_heavy",
		8.5f,
		3,
		20,
		STANDARD_RIFLE_BULLET,
		50,
		0.25f,
		new float[] {1.25f, 3.5f},
		1,
		GunItem.LoadingType.MAGAZINE,
		null,
		ModSounds.RELOAD_HEAVY_AR_P1,
		ModSounds.RELOAD_HEAVY_AR_P2,
		ModSounds.RELOAD_HEAVY_AR_P3,
		ModSounds.ASSAULTRIFLE_HEAVY,
		1,
		false,
		5,
		21,
		32)
	{
	});
    public static final Item WAR_TORN_ASSAULT_RIFLE = registerItem("assaultrifle_rus", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
			"assaultrifle_rus",
			"assaultrifle_rus",
            7.5f,
            2,
            30,
            STANDARD_RIFLE_BULLET,
            50,
            1.5f,
			new float[] {2.25f, 3f},
            1,
			GunItem.LoadingType.MAGAZINE,
			null,
			ModSounds.RELOAD_WAR_TORN_AR_P1,
			ModSounds.RELOAD_WAR_TORN_AR_P2,
			ModSounds.RELOAD_WAR_TORN_AR_P3,
            ModSounds.ASSAULTRIFLE_RUS,
            1,
            false,
            10,
            24,
            39)
    {
    });

	public static final Item DOUBLE_BARRELED_SHOTGUN = registerItem("shotgun_doublebarrel", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
			"shotgun_doublebarrel",
			"shotgun_doublebarrel",
			7f,
			4,
			2,
			SHOTGUN_SHELL,
			44,
			7.5f,
			new float[] {0.5f, 15f},
			5,
			GunItem.LoadingType.MAGAZINE,
			null,
			ModSounds.RELOAD_DOUBLE_BARREL_SHOTGUN_P1,
			ModSounds.RELOAD_DOUBLE_BARREL_SHOTGUN_P2,
			ModSounds.RELOAD_DOUBLE_BARREL_SHOTGUN_P3,
			ModSounds.SHOTGUN_DOUBLEBARREL,
			1,
			false,
			11,
			12,
			30)
	{
	});

	public static final Item COMBAT_SHOTGUN = registerItem("shotgun_combat", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
		"shotgun_combat",
		"shotgun_combat",
        5.5f,
        15,
        6,
        SHOTGUN_SHELL,
        26,
        3.75f,
		new float[] {1f, 8.25f},
        5,
		GunItem.LoadingType.PER_CARTRIDGE,
		null,
		ModSounds.RELOAD_COMBAT_SHOTGUN_P1,
		ModSounds.RELOAD_COMBAT_SHOTGUN_P2,
		ModSounds.RELOAD_COMBAT_SHOTGUN_P3,
        ModSounds.SHOTGUN_COMBAT,
        6,
        false,
        4,
        5,
        15)
	{
	});

    public static final Item RIOT_SHOTGUN = registerItem("shotgun_riot", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
            "shotgun_riot",
            "shotgun_riot",
            2f,
            5,
            8,
            SHOTGUN_SHELL,
            28,
            6.25f,
            new float[] {2f, 6.25f},
            10,
            GunItem.LoadingType.PER_CARTRIDGE,
            null,
            ModSounds.RELOAD_RIOT_SHOTGUN_P1,
            ModSounds.RELOAD_RIOT_SHOTGUN_P2,
            ModSounds.RELOAD_RIOT_SHOTGUN_P3,
            ModSounds.SHOTGUN_RIOT,
            8,
            false,
            4,
            5,
            17)
    {
    });

	public static final Item CLASSIC_SNIPER_RIFLE = registerItem("sniper_classic", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
		"sniper_classic",
		"sniper_classic",
        22,
        20,
        5,
        HEAVY_RIFLE_BULLET,
        36,
        0.125f,
		new float[]{2.5f, 8.25f},
        1,
		GunItem.LoadingType.PER_CARTRIDGE,
		null,
		ModSounds.RELOAD_GENERIC_SNIPER_P1,
		ModSounds.RELOAD_CLASSIC_SNIPER_P2,
		ModSounds.RELOAD_GENERIC_SNIPER_P3,
        ModSounds.SNIPER_CLASSIC,
        5,
        true,
        12,
        13,
        24)
	{
	});

	public static final Item BRUSH_GUN = registerItem("sniper_cowboy", new GunItem(new FabricItemSettings().group(AnimatedGuns.GUNS).maxCount(1),
			"sniper_cowboy",
			"sniper_cowboy",
			12,
			12,
			10,
			HEAVY_HANDGUN_BULLET,
			36,
			0.5f,
			new float[] {1.75f, 5.25f},
			1,
			GunItem.LoadingType.PER_CARTRIDGE,
			null,
			ModSounds.RELOAD_BRUSH_GUN_P1,
			ModSounds.RELOAD_BRUSH_GUN_P2,
			ModSounds.RELOAD_BRUSH_GUN_P3,
			ModSounds.SNIPER_COWBOY,
			10,
			false,
			8,
			9,
			21)
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
