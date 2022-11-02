package net.elidhan.anim_guns.client.model;

import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.item.gun.HeavyAssaultRifleItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HeavyAssaultRifleModel extends AnimatedGeoModel<HeavyAssaultRifleItem>
{
    @Override
    public Identifier getModelResource(HeavyAssaultRifleItem object)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "geo/assaultrifle_heavy.geo.json");
    }

    @Override
    public Identifier getTextureResource(HeavyAssaultRifleItem object)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "textures/gun/assaultrifle_heavy.png");
    }

    @Override
    public Identifier getAnimationResource(HeavyAssaultRifleItem animatable)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "animations/assaultrifle_generic.animation.json");
    }
}
