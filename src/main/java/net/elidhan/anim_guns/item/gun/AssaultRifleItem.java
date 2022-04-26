package net.elidhan.anim_guns.item.gun;

import net.elidhan.anim_guns.item.ModItems;
import net.minecraft.item.Item;

public class AssaultRifleItem extends GunTemplateItem {

    @Override
    public Item reqAmmo() {
        return ModItems.STANDARD_RIFLE_BULLET;
    }

    public float dmg(){
        return 7.0f;
    }

    @Override
    public float reloadCD() {
        return 48.0f;
    }
    @Override
    public int useCD() {
        return 2;
    }
    @Override
    public int clipSize() {
        return 30;
    }
    @Override
    public float recoil() {
        return 2.75f;
    }
    @Override
    public float recoilMult(){ return 0.6f;}
    @Override
    public float spread() {
        return 1.5f;
    }

    public AssaultRifleItem(Settings settings) {
        super(settings);
    }
}
