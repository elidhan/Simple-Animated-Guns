package net.elidhan.anim_guns.client.render;

import net.elidhan.anim_guns.client.model.WarTornAssaultRifleModel;
import net.elidhan.anim_guns.item.gun.WarTornAssaultRifleItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class WarTornAssaultRifleRenderer extends GeoItemRenderer<WarTornAssaultRifleItem>
{
    public WarTornAssaultRifleRenderer()
    {
        super(new WarTornAssaultRifleModel());
    }
}
