package net.elidhan.anim_guns.client.model;

import mod.azure.azurelib.model.DefaultedItemGeoModel;
import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.item.GunItem;
import net.minecraft.util.Identifier;

public class GunModel extends DefaultedItemGeoModel<GunItem>
{
    /**
     * Create a new instance of this model class.<br>
     * The asset path should be the truncated relative path from the base folder.<br>
     * E.G.
     * <pre>{@code
     * 	new ResourceLocation("myMod", "armor/obsidian")
     * }</pre>
     *
     * @param assetSubpath
     */
    public GunModel(Identifier assetSubpath) {
        super(assetSubpath);
    }

    @Override
    public Identifier getModelResource(GunItem object)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "geo/"+object.getID()+".geo.json");
    }

    @Override
    public Identifier getTextureResource(GunItem object)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "textures/gun/"+object.getID()+".png");
    }

    @Override
    public Identifier getAnimationResource(GunItem animatable)
    {
        return new Identifier(AnimatedGuns.MOD_ID, "animations/"+animatable.getAnimationID()+".animation.json");
    }
}
