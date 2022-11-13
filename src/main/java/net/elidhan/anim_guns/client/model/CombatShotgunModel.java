package net.elidhan.anim_guns.client.model;

import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.item.gun.CombatShotgunItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CombatShotgunModel extends AnimatedGeoModel<CombatShotgunItem>
{
    @Override
    public Identifier getModelResource(CombatShotgunItem object)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "geo/shotgun_combat.geo.json");
    }

    @Override
    public Identifier getTextureResource(CombatShotgunItem object)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "textures/gun/shotgun_combat.png");
    }

    @Override
    public Identifier getAnimationResource(CombatShotgunItem animatable)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "animations/shotgun_combat.animation.json");
    }
}
