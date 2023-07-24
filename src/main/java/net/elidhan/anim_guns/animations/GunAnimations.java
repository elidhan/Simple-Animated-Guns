package net.elidhan.anim_guns.animations;

import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;

public final class GunAnimations {
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    public static final RawAnimation FIRING = RawAnimation.begin().thenPlay("firing");
    public static final RawAnimation RELOAD_START = RawAnimation.begin().thenPlay("reload_start");
    public static final RawAnimation RELOAD_MAGOUT = RawAnimation.begin().thenPlay("reload_magout");
    public static final RawAnimation RELOAD_MAGIN = RawAnimation.begin().thenPlay("reload_magin");
    public static final RawAnimation RELOAD_END = RawAnimation.begin().thenPlay("reload_end");
    public static final RawAnimation AIM = RawAnimation.begin().thenLoop("aim");
    public static final RawAnimation AIM_FIRING = RawAnimation.begin().thenPlay("aim_firing");
    public static final RawAnimation AIM_RELOAD_START = RawAnimation.begin().thenPlay("aim_reload_start");
    public static final RawAnimation MELEE = RawAnimation.begin().thenPlay("melee");
    public static final RawAnimation SPRINTING = RawAnimation.begin().thenLoop("sprinting");

    public static <T extends GeoAnimatable> AnimationController<T> genericGunController(T animatable) {
        return new AnimationController<>(animatable, "controller", 0, state -> {
            return state.setAndContinue(IDLE);
        });
    }
}
