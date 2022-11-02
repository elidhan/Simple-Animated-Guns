package net.elidhan.anim_guns.client.model;

import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.item.gun.AssaultRifleItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AssaultRifleModel extends AnimatedGeoModel<AssaultRifleItem>
{
    @Override
    public Identifier getModelResource(AssaultRifleItem object)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "geo/assaultrifle_light.geo.json");
    }

    @Override
    public Identifier getTextureResource(AssaultRifleItem object)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "textures/gun/assaultrifle_light.png");
    }

    @Override
    public Identifier getAnimationResource(AssaultRifleItem animatable)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "animations/assaultrifle_generic.animation.json");
    }
}
