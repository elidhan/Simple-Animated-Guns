package net.elidhan.anim_guns.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;

@Environment(EnvType.CLIENT)
public class ModPlayerEntityRenderer extends PlayerEntityRenderer
{
    public ModPlayerEntityRenderer(EntityRendererFactory.Context ctx, boolean slim)
    {
        super(ctx, slim);
    }
}
