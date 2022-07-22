package net.elidhan.anim_guns.item.gun;

import net.elidhan.anim_guns.item.ModItems;
import net.minecraft.item.Item;

public class PistolGunItem extends GunTemplateItem
{
    @Override
    public Item reqAmmo() {
        return ModItems.STANDARD_HANDGUN_BULLET;
    }
    public float dmg()
    {
        return 5.0f;
    }

    @Override
    public double range()
    {
        return 32;
    }

    @Override
    public int rps()
    {
        return 1;
    }
    @Override
    public float reloadCD() {
        return 30.0F;
    }
    @Override
    public int reloadStageTwo() {
        return 1;
    }
    @Override
    public int reloadStageThree() {
        return 30;
    }
    @Override
    public int reloadCycles() {
        return 1;
    }
    @Override
    public int useCD() {
        return 4;
    }
    @Override
    public int clipSize() {
        return 12;
    }
    @Override
    public float recoil() {
        return 2.75f;
    }
    @Override
    public float spread() {
        return 2.0f;
    }
    public PistolGunItem(Settings settings) {
        super(settings);
    }
}
