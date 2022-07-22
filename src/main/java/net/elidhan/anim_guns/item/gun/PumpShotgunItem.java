package net.elidhan.anim_guns.item.gun;

import net.elidhan.anim_guns.item.ModItems;
import net.minecraft.item.Item;

public class PumpShotgunItem extends GunTemplateItem
{
    @Override
    public Item reqAmmo()
    {
        return ModItems.SHOTGUN_SHELL;
    }
    @Override
    public float reloadCD()
    {
        return 21;
    }
    @Override
    public int rps()
    {
        return 10;
    }
    @Override
    public double range()
    {
        return 8;
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
        return 2.5f;
    }
    @Override
    public float spread()
    {
        return 12.5f;
    }
    @Override
    public float recoil()
    {
        return 6.5f;
    }
    @Override
    public int clipSize()
    {
        return 6;
    }
    public PumpShotgunItem(Settings settings)
    {
        super(settings);
    }

}
