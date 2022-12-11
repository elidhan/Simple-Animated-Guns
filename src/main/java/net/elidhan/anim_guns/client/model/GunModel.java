package net.elidhan.anim_guns.client.model;

import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.item.GunItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GunModel extends AnimatedGeoModel<GunItem>
{
    @Override
    public Identifier getModelLocation(GunItem object)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "geo/"+object.getID()+".geo.json");
    }

    @Override
    public Identifier getTextureLocation(GunItem object)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "textures/gun/"+object.getID()+".png");
    }

    @Override
    public Identifier getAnimationFileLocation(GunItem animatable)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "animations/"+animatable.getAnimationID()+".animation.json");
    }
}
