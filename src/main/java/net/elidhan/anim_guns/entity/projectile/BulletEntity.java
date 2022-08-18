package net.elidhan.anim_guns.entity.projectile;

import net.elidhan.anim_guns.AnimatedGuns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BulletEntity extends ThrownItemEntity
{
    private Vec3d accel;
    private float damage;
    private int lifeTick;
    public BulletEntity(EntityType<? extends ThrownItemEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public BulletEntity(LivingEntity livingEntity, World world, float dmg)
    {
        super(AnimatedGuns.BulletEntityType, livingEntity, world);
        this.damage = dmg;
        this.lifeTick = 0;
        this.setNoGravity(true);
    }

    @Override
    protected ItemStack getItem()
    {
        return new ItemStack(Items.BLACKSTONE);
    }

    @Override
    protected Item getDefaultItem()
    {
        return Items.BLACKSTONE;
    }

    @Override
    public void tick()
    {
        super.tick();
        this.lifeTick++;

        if(accel != null)
            this.setVelocity(accel);

        if(this.lifeTick >= 32)
        {
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult)
    {
        super.onEntityHit(entityHitResult);
        if (this.world.isClient || (entityHitResult.getEntity() instanceof WitherEntity && ((WitherEntity)entityHitResult.getEntity()).shouldRenderOverlay())) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), this.damage);
        entity.timeUntilRegen = 0;
    }

    @Override
    protected void onCollision(HitResult hitResult)
    {
        super.onCollision(hitResult);
        if (!world.isClient())
        {
            this.discard();
        }
    }

    public void setAccel(Vec3d velocity)
    {
        this.accel = velocity;
    }
}
