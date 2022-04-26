package net.elidhan.anim_guns.entity.projectile;

import net.elidhan.anim_guns.AnimatedGuns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class BulletEntity extends ThrownItemEntity
{
    private float damage;

    public BulletEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public BulletEntity(World world, LivingEntity owner, float dmg) {
        super(AnimatedGuns.BulletEntityType, owner, world);
        damage = dmg;
    }

    public BulletEntity(World world, double x, double y, double z) {
        super(AnimatedGuns.BulletEntityType, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.BLACKSTONE;
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
