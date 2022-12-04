package net.elidhan.anim_guns.particle;

import net.elidhan.anim_guns.AnimatedGuns;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModParticles
{
    public static final DefaultParticleType MUZZLE_FLASH_PARTICLE_1 = FabricParticleTypes.simple();
    public static final DefaultParticleType MUZZLE_FLASH_PARTICLE_2 = FabricParticleTypes.simple();

    public static void registerParticles()
    {
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(AnimatedGuns.MOD_ID, "muzzle_flash_1"), MUZZLE_FLASH_PARTICLE_1);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(AnimatedGuns.MOD_ID, "muzzle_flash_2"), MUZZLE_FLASH_PARTICLE_2);
    }
}
