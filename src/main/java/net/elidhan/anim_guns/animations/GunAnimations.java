package net.elidhan.anim_guns.animations;

import mod.azure.azurelib.core.animation.RawAnimation;

public final class GunAnimations {
    public static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    public static final RawAnimation FIRING = RawAnimation.begin().thenPlay("firing");
    public static final RawAnimation RELOAD_START = RawAnimation.begin().thenPlay("reload_start");
    public static final RawAnimation RELOAD_MAGOUT = RawAnimation.begin().thenPlay("reload_magout");
    public static final RawAnimation RELOAD_MAGIN = RawAnimation.begin().thenPlay("reload_magin");
    public static final RawAnimation RELOAD_END = RawAnimation.begin().thenPlay("reload_end");
    public static final RawAnimation AIM = RawAnimation.begin().thenPlay("aim");
    public static final RawAnimation AIM_FIRING = RawAnimation.begin().thenPlay("aim_firing");
    public static final RawAnimation AIM_RELOAD_START = RawAnimation.begin().thenPlay("aim_reload_start");
    public static final RawAnimation MELEE = RawAnimation.begin().thenPlay("melee");
    public static final RawAnimation SPRINTING = RawAnimation.begin().thenPlay("sprinting");
}
