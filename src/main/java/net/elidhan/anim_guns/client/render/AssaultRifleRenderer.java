package net.elidhan.anim_guns.client.render;

import net.elidhan.anim_guns.item.gun.AssaultRifleItem;
import net.elidhan.anim_guns.client.model.AssaultRifleModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class AssaultRifleRenderer extends GeoItemRenderer<AssaultRifleItem>
{
    public AssaultRifleRenderer()
    {
        super(new AssaultRifleModel());
    }
}
