package net.elidhan.anim_guns.client.render;

import net.elidhan.anim_guns.client.model.HeavyAssaultRifleModel;
import net.elidhan.anim_guns.item.gun.HeavyAssaultRifleItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class HeavyAssaultRifleRenderer extends GeoItemRenderer<HeavyAssaultRifleItem>
{
    public HeavyAssaultRifleRenderer()
    {
        super(new HeavyAssaultRifleModel());
    }
}
