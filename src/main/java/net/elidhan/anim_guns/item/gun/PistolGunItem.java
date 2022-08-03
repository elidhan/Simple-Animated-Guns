package net.elidhan.anim_guns.item.gun;

import net.elidhan.anim_guns.item.ModItems;
import net.elidhan.anim_guns.sound.ModSounds;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;

public class PistolGunItem extends GunTemplateItem
{
    @Override
    protected Item reqAmmo() {
        return ModItems.STANDARD_HANDGUN_BULLET;
    }
    protected float dmg()
    {
        return 5.0f;
    }
    @Override
    protected int rps()
    {
        return 1;
    }
    @Override
    protected float reloadCD() {
        return 30.0F;
    }
    @Override
    protected int reloadStageOne()
    {
        return 1;
    }
    @Override
    protected int reloadStageTwo() {
        return 11;
    }
    @Override
    protected int reloadStageThree() {
        return 24;
    }
    @Override
    protected SoundEvent reload_p1()
    {
        return ModSounds.RELOAD_GENERIC_AR_P1;
    }
    @Override
    protected SoundEvent reload_p2()
    {
        return ModSounds.RELOAD_GENERIC_AR_P2;
    }
    @Override
    protected SoundEvent reload_p3()
    {
        return ModSounds.RELOAD_GENERIC_AR_P3;
    }
    @Override
    protected SoundEvent shootSound()
    {
        return ModSounds.ASSAULTRIFLE_LIGHT;
    }
    @Override
    protected int reloadCycles() {
        return 1;
    }
    @Override
    protected boolean hasScope()
    {
        return false;
    }
    @Override
    protected int loadingType()
    {
        return 1;
    }
    @Override
    protected int useCD() {
        return 5;
    }
    @Override
    protected int clipSize() {
        return 12;
    }
    @Override
    protected float recoil() {
        return 2.75f;
    }
    @Override
    protected float spread() {
        return 2.0f;
    }
    public PistolGunItem(Settings settings) {
        super(settings);
    }
}
