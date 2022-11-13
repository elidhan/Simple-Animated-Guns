package net.elidhan.anim_guns.client.render;

import net.elidhan.anim_guns.client.model.CombatShotgunModel;
import net.elidhan.anim_guns.item.gun.CombatShotgunItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class CombatShotgunRenderer extends GeoItemRenderer<CombatShotgunItem>
{
    public CombatShotgunRenderer()
    {
        super(new CombatShotgunModel());
    }
}
