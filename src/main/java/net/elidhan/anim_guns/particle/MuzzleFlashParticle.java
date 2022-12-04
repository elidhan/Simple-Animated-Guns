package net.elidhan.anim_guns.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

import javax.annotation.Nullable;

@Environment(value=EnvType.CLIENT)
public class MuzzleFlashParticle
extends SpriteBillboardParticle
{
    protected MuzzleFlashParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteSet, double dx, double dy, double dz)
    {
        super(clientWorld, d, e, f, dx, dy, dz);
        this.velocityMultiplier = 0;
        this.scale(4.0f);
        this.maxAge = 1;
        this.gravityStrength = 0;
        this.setSpriteForAge(spriteSet);
        this.alpha = 0.55f;

        this.red = 1f;
        this.blue = 1f;
        this.green = 1f;
    }

    @Override
    public void tick()
    {
        if (this.age++ >= this.maxAge) {
            this.markDead();
        }
    }

    @Override
    public float getSize(float tickDelta)
    {
        return super.getSize(tickDelta);
    }

    @Override
    public ParticleTextureSheet getType()
    {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet)
        {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
        {
            return new MuzzleFlashParticle(world, x, y , z, this.sprites, velocityX, velocityY, velocityZ);
        }
    }
}
