package net.elidhan.anim_guns.client.model;

import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.item.gun.WarTornAssaultRifleItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WarTornAssaultRifleModel extends AnimatedGeoModel<WarTornAssaultRifleItem>
{
    @Override
    public Identifier getModelResource(WarTornAssaultRifleItem object)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "geo/assaultrifle_rus.geo.json");
    }

    @Override
    public Identifier getTextureResource(WarTornAssaultRifleItem object)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "textures/gun/assaultrifle_rus.png");
    }

    @Override
    public Identifier getAnimationResource(WarTornAssaultRifleItem animatable)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "animations/assaultrifle_rus.animation.json");
    }
}
