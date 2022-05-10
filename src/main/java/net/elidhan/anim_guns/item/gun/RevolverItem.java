package net.elidhan.anim_guns.item.gun;

import net.elidhan.anim_guns.item.ModItems;
import net.minecraft.item.Item;

public class RevolverItem extends GunTemplateItem{
    @Override
    public Item reqAmmo() {
        return ModItems.HEAVY_HANDGUN_BULLET;
    }
    public float dmg(){
        return 10.0f;
    }
    @Override
    public float reloadCD() {
        return 44.0F;
    }

    @Override
    public int reloadStageOne() {
        return 48;
    }

    @Override
    public int reloadStageTwo() {
        return 0;
    }

    @Override
    public int reloadStageThree() {
        return 0;
    }

    @Override
    public int reloadCycles() {
        return 1;
    }

    @Override
    public int useCD() {
        return 12;
    }
    @Override
    public int clipSize() {
        return 6;
    }
    @Override
    public float recoil() {
        return 5.25f;
    }
    @Override
    public float recoilMult(){ return 0.75f;}
    @Override
    public float spread() {
        return 0.5f;
    }
    public RevolverItem(Settings settings) {
        super(settings);
    }

}
