package net.elidhan.anim_guns.entity.projectile;

import net.elidhan.anim_guns.AnimatedGuns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class BulletEntity extends ThrownItemEntity
{
    private float damage;
    private int lifeTicks = 0;
    public BulletEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }
    public BulletEntity(World world, LivingEntity owner, float dmg) {
        super(AnimatedGuns.BulletEntityType, owner, world);
        this.setOwner(owner);
        this.setNoGravity(true);
        this.damage = dmg;
    }
    @Override
    public boolean isFireImmune()
    {
        return true;
    }
    @Override
    public boolean isImmuneToExplosion()
    {
        return true;
    }
    @Override
    protected Item getDefaultItem() {
        return null;
    }
    @Override
    public void tick()
    {
        lifeTicks++;

        if(lifeTicks >= 20)
        {
            this.discard();
            return;
        }
        super.tick();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        Entity entity = entityHitResult.getEntity();
        entity.damage(DamageSource.thrownProjectile(this,this.getOwner()), damage);
        entity.timeUntilRegen = 0;
        this.discard();
    }
    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if(!this.world.isClient())
        {
            this.discard();
        }
    }
}
