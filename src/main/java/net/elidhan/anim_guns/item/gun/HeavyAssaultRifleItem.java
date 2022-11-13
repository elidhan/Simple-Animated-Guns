package net.elidhan.anim_guns.item.gun;

import net.elidhan.anim_guns.item.GunItem;
import net.elidhan.anim_guns.sound.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;

public class HeavyAssaultRifleItem extends GunItem
{
    public HeavyAssaultRifleItem(Settings settings, float gunDamage, int rateOfFire, int magSize, Item ammoType, int reloadCooldown, float bulletSpread, float gunRecoil, int pelletCount, int loadingType, SoundEvent reload1, SoundEvent reload2, SoundEvent reload3, SoundEvent shootSound, int reloadCycles, boolean isScoped, int reloadStage1, int reloadStage2, int reloadStage3)
    {
        super(settings, gunDamage, rateOfFire, magSize, ammoType, reloadCooldown, bulletSpread, gunRecoil, pelletCount, loadingType, reload1, reload2, reload3, shootSound, reloadCycles, isScoped, reloadStage1, reloadStage2, reloadStage3);
    }
    @Override
    protected <ENTITY extends IAnimatable> void soundListener(SoundKeyframeEvent<ENTITY> event) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            switch (event.sound)
            {
                case "generic_ar_p1" ->
                        MinecraftClient.getInstance().player.playSound(ModSounds.RELOAD_HEAVY_AR_P1, SoundCategory.MASTER, 1, 1);
                case "generic_ar_p2" ->
                        MinecraftClient.getInstance().player.playSound(ModSounds.RELOAD_HEAVY_AR_P2, SoundCategory.MASTER, 1, 1);
                case "generic_ar_p3" ->
                        MinecraftClient.getInstance().player.playSound(ModSounds.RELOAD_HEAVY_AR_P3, SoundCategory.MASTER, 1, 1);
            }
        }
    }
}
