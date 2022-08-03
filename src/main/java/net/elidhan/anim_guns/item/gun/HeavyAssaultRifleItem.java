package net.elidhan.anim_guns.item.gun;

import net.elidhan.anim_guns.item.ModItems;
import net.elidhan.anim_guns.sound.ModSounds;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;

public class HeavyAssaultRifleItem extends GunTemplateItem
{
    public HeavyAssaultRifleItem(Settings settings)
    {
        super(settings);
    }

    @Override
    protected Item reqAmmo()
    {
        return ModItems.STANDARD_RIFLE_BULLET;
    }

    @Override
    protected float reloadCD()
    {
        return 68;
    }

    @Override
    protected int reloadStageOne()
    {
        return 6;
    }

    @Override
    protected int reloadStageTwo()
    {
        return 43;
    }

    @Override
    protected int reloadStageThree()
    {
        return 60;
    }

    @Override
    protected SoundEvent reload_p1()
    {
        return ModSounds.RELOAD_HEAVY_AR_P1;
    }

    @Override
    protected SoundEvent reload_p2()
    {
        return ModSounds.RELOAD_HEAVY_AR_P2;
    }

    @Override
    protected SoundEvent reload_p3()
    {
        return ModSounds.RELOAD_HEAVY_AR_P3;
    }

    @Override
    protected SoundEvent shootSound()
    {
        return ModSounds.ASSAULTRIFLE_HEAVY;
    }

    @Override
    protected int loadingType()
    {
        return 1;
    }

    @Override
    protected int reloadCycles()
    {
        return 1;
    }

    @Override
    protected boolean hasScope()
    {
        return false;
    }

    @Override
    protected int useCD()
    {
        return 3;
    }

    @Override
    protected float dmg()
    {
        return 8.5f;
    }

    @Override
    protected int rps()
    {
        return 1;
    }

    @Override
    protected float spread()
    {
        return 1;
    }

    @Override
    protected float recoil()
    {
        return 3.25f;
    }

    @Override
    protected int clipSize()
    {
        return 20;
    }
}
