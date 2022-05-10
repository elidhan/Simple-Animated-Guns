package net.elidhan.anim_guns.item.gun;

import net.elidhan.anim_guns.item.ModItems;
import net.minecraft.item.Item;

public class PistolGunItem extends GunTemplateItem{

    @Override
    public Item reqAmmo() {
        return ModItems.STANDARD_HANDGUN_BULLET;
    }
    public float dmg(){
        return 5.0f;
    }
    @Override
    public float reloadCD() {
        return 34.0F;
    }

    @Override
    public int reloadStageOne() {
        return 32;
    }

    @Override
    public int reloadStageTwo() {
        return 28;
    }

    @Override
    public int reloadStageThree() {
        return 5;
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
    public float recoilMult(){ return 0.5f;}

    @Override
    public float spread() {
        return 2.0f;
    }
    public PistolGunItem(Settings settings) {
        super(settings);
    }

}
