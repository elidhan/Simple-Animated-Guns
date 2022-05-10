package net.elidhan.anim_guns.item.gun;

import net.elidhan.anim_guns.item.ModItems;
import net.minecraft.item.Item;

public class PumpShotgunItem extends GunTemplateItem
{

    public PumpShotgunItem(Settings settings)
    {
        super(settings);
    }

    @Override
    public Item reqAmmo()
    {
        return ModItems.HEAVY_RIFLE_BULLET;
    }

    @Override
    public float reloadCD()
    {
        return 21;
    }

    @Override
    public int reloadStageOne()
    {
        return 1;
    }

    @Override
    public int reloadStageTwo()
    {
        return 6;
    }

    @Override
    public int reloadStageThree()
    {
        return 13;
    }

    @Override
    public int reloadCycles()
    {
        return clipSize();
    }

    @Override
    public int useCD()
    {
        return 10;
    }

    @Override
    public float dmg()
    {
        return 5;
    }

    @Override
    public float spread()
    {
        return 0.5f;
    }

    @Override
    public float recoil()
    {
        return 6.5f;
    }

    @Override
    public float recoilMult()
    {
        return 0.5f;
    }

    @Override
    public int clipSize()
    {
        return 5;
    }
}
