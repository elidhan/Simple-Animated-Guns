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
import net.minecraft.util.math.Vec3d;

public class BulletEntity extends ThrownItemEntity
{
    private float damage;
    private int lifeTicks;
    private double maxRange;
    private Vec3d startPos;

    public BulletEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }
    public BulletEntity(World world, LivingEntity owner, float dmg, double range, Vec3d pos) {
        super(AnimatedGuns.BulletEntityType, owner, world);
        this.setNoGravity(true);
        this.damage = dmg;
        this.maxRange = range;
        this.startPos = pos;
    }
    /*public BulletEntity(World world, double x, double y, double z) {
        super(AnimatedGuns.BulletEntityType, x, y, z, world);
    }*/
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
    public void tick() {
        lifeTicks++;
        Vec3d currPos = this.getPos();
        /*double travelled = currPos.distanceTo(startPos);*/

        if(lifeTicks >= 100) //|| travelled > maxRange
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
