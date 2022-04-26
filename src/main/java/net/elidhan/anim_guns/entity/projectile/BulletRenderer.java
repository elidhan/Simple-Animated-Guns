package net.elidhan.anim_guns.entity.projectile;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class BulletRenderer extends EntityRenderer<BulletEntity> {
    public BulletRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(BulletEntity entity) {
        return null;
    }
}
