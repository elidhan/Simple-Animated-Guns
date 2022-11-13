package net.elidhan.anim_guns.client.render;

import net.elidhan.anim_guns.client.model.GunModel;
import net.elidhan.anim_guns.item.GunItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class GunRenderer extends GeoItemRenderer<GunItem>
{
    public GunRenderer()
    {
        super(new GunModel());
    }
}
