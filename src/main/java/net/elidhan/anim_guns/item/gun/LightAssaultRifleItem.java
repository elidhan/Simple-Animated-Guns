package net.elidhan.anim_guns.item.gun;

import net.elidhan.anim_guns.item.ModItems;
import net.minecraft.item.Item;

public class LightAssaultRifleItem extends GunTemplateItem
{
    public LightAssaultRifleItem(Settings settings)
    {
        super(settings);
    }

    @Override
    public Item reqAmmo()
    {
        return ModItems.STANDARD_RIFLE_BULLET;
    }

    @Override
    public float reloadCD()
    {
        return 44;
    }

    @Override
    public int reloadStageTwo()
    {
        return 4;
    }

    @Override
    public int reloadStageThree()
    {
        return 44;
    }

    @Override
    public int reloadCycles()
    {
        return 1;
    }

    @Override
    public int useCD()
    {
        return 3;
    }

    @Override
    public float dmg()
    {
        return 5;
    }

    @Override
    public double range()
    {
        return 64;
    }

    @Override
    public int rps()
    {
        return 1;
    }

    @Override
    public float spread()
    {
        return 0.5f;
    }

    @Override
    public float recoil()
    {
        return 0.5f;
    }

    @Override
    public int clipSize()
    {
        return 20;
    }
}
