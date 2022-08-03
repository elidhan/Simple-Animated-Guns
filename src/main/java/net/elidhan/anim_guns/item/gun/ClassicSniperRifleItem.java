package net.elidhan.anim_guns.item.gun;

import net.elidhan.anim_guns.item.ModItems;
import net.elidhan.anim_guns.sound.ModSounds;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;

public class ClassicSniperRifleItem extends GunTemplateItem
{
    public ClassicSniperRifleItem(Settings settings)
    {
        super(settings);
    }
    @Override
    protected Item reqAmmo()
    {
        return ModItems.HEAVY_RIFLE_BULLET;
    }
    @Override
    protected float reloadCD()
    {
        return 26;
    }
    @Override
    protected int reloadStageOne()
    {
        return 1;
    }
    @Override
    protected int reloadStageTwo()
    {
        return 8;
    }
    @Override
    protected int reloadStageThree()
    {
        return 17;
    }
    @Override
    protected SoundEvent reload_p1()
    {
        return ModSounds.RELOAD_GENERIC_SNIPER_P1;
    }
    @Override
    protected SoundEvent reload_p2()
    {
        return ModSounds.RELOAD_CLASSIC_SNIPER_P2;
    }
    @Override
    protected SoundEvent reload_p3()
    {
        return ModSounds.RELOAD_GENERIC_SNIPER_P3;
    }
    @Override
    protected SoundEvent shootSound()
    {
        return ModSounds.SNIPER_CLASSIC;
    }
    @Override
    protected int loadingType()
    {
        return 2;
    }
    @Override
    protected int reloadCycles()
    {
        return clipSize();
    }
    @Override
    protected boolean hasScope()
    {
        return true;
    }
    @Override
    protected int useCD()
    {
        return 20;
    }
    @Override
    protected float dmg()
    {
        return 20;
    }
    @Override
    protected int rps()
    {
        return 1;
    }
    @Override
    protected float spread()
    {
        return 0;
    }
    @Override
    protected float recoil()
    {
        return 7.5f;
    }
    @Override
    protected int clipSize()
    {
        return 5;
    }
}
