package net.elidhan.anim_guns.item.gun;

import net.elidhan.anim_guns.item.ModItems;
import net.elidhan.anim_guns.sound.ModSounds;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;

public class CombatShotgunItem extends GunTemplateItem
{
    @Override
    protected Item reqAmmo()
    {
        return ModItems.SHOTGUN_SHELL;
    }
    @Override
    protected float reloadCD()
    {
        return 26;
    }
    @Override
    protected int rps()
    {
        return 5;
    }
    @Override
    protected int reloadStageOne()
    {
        return 1;
    }
    @Override
    protected int reloadStageTwo()
    {
        return 4;
    }
    @Override
    protected int reloadStageThree()
    {
        return 13;
    }
    @Override
    protected int reloadCycles()
    {
        return clipSize();
    }
    @Override
    protected boolean hasScope()
    {
        return false;
    }
    @Override
    protected int useCD()
    {
        return 14;
    }
    @Override
    protected int loadingType()
    {
        return 2;
    }
    @Override
    protected SoundEvent reload_p1()
    {
        return ModSounds.RELOAD_COMBAT_SHOTGUN_P1;
    }
    @Override
    protected SoundEvent reload_p2()
    {
        return ModSounds.RELOAD_COMBAT_SHOTGUN_P2;
    }
    @Override
    protected SoundEvent reload_p3()
    {
        return ModSounds.RELOAD_COMBAT_SHOTGUN_P3;
    }
    @Override
    protected SoundEvent shootSound()
    {
        return ModSounds.SHOTGUN_COMBAT;
    }
    @Override
    protected float dmg()
    {
        return 5.5f;
    }
    @Override
    protected float spread()
    {
        return 12.5f;
    }
    @Override
    protected float recoil()
    {
        return 6.25f;
    }
    @Override
    protected int clipSize()
    {
        return 6;
    }
    public CombatShotgunItem(Settings settings)
    {
        super(settings);
    }

}
