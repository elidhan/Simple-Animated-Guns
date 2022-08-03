package net.elidhan.anim_guns.sound;

import net.elidhan.anim_guns.AnimatedGuns;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModSounds
{
    public static SoundEvent RELOAD_GENERIC_AR_P1 = registerSoundEvent("generic_ar_p1");
    public static SoundEvent RELOAD_GENERIC_AR_P2 = registerSoundEvent("generic_ar_p2");
    public static SoundEvent RELOAD_GENERIC_AR_P3 = registerSoundEvent("generic_ar_p3");
    public static SoundEvent ASSAULTRIFLE_LIGHT = registerSoundEvent("assaultrifle_light");
    public static SoundEvent RELOAD_HEAVY_AR_P1 = registerSoundEvent("heavy_ar_p1");
    public static SoundEvent RELOAD_HEAVY_AR_P2 = registerSoundEvent("heavy_ar_p2");
    public static SoundEvent RELOAD_HEAVY_AR_P3 = registerSoundEvent("heavy_ar_p3");
    public static SoundEvent ASSAULTRIFLE_HEAVY = registerSoundEvent("assaultrifle_heavy");
    public static SoundEvent RELOAD_COMBAT_SHOTGUN_P1 = registerSoundEvent("combat_shotgun_p1");
    public static SoundEvent RELOAD_COMBAT_SHOTGUN_P2 = registerSoundEvent("combat_shotgun_p2");
    public static SoundEvent RELOAD_COMBAT_SHOTGUN_P3 = registerSoundEvent("combat_shotgun_p3");
    public static SoundEvent SHOTGUN_COMBAT = registerSoundEvent("shotgun_combat");
    public static SoundEvent RELOAD_GENERIC_SNIPER_P1 = registerSoundEvent("generic_sniper_p1");
    public static SoundEvent RELOAD_GENERIC_SNIPER_P2 = registerSoundEvent("generic_sniper_p2");
    public static SoundEvent RELOAD_GENERIC_SNIPER_P3 = registerSoundEvent("generic_sniper_p3");
    public static SoundEvent RELOAD_CLASSIC_SNIPER_P2 = registerSoundEvent("classic_sniper_p2");
    public static SoundEvent SNIPER_CLASSIC = registerSoundEvent("sniper_classic");
    private static SoundEvent registerSoundEvent(String name)
    {
        Identifier id = new Identifier(AnimatedGuns.MOD_ID, name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

    public static void registerSounds()
    {
        AnimatedGuns.LOGGER.info("Registering ModSounds for " + AnimatedGuns.MOD_ID);
    }
}
