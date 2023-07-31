package net.elidhan.anim_guns.animations;

import mod.azure.azurelib.core.animation.Animation;
import mod.azure.azurelib.core.animation.RawAnimation;

public final class GunAnimations {
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    public static final RawAnimation FIRING = RawAnimation.begin().then("firing", Animation.LoopType.HOLD_ON_LAST_FRAME);//.thenLoop("idle");
    public static final RawAnimation RELOAD_START = RawAnimation.begin().then("reload_start", Animation.LoopType.PLAY_ONCE);
    public static final RawAnimation RELOAD_MAGOUT = RawAnimation.begin().then("reload_magout", Animation.LoopType.PLAY_ONCE);
    public static final RawAnimation RELOAD_MAGIN = RawAnimation.begin().then("reload_magin", Animation.LoopType.PLAY_ONCE);
    public static final RawAnimation RELOAD_END = RawAnimation.begin().then("reload_end", Animation.LoopType.HOLD_ON_LAST_FRAME);
    public static final RawAnimation AIM = RawAnimation.begin().thenLoop("aim");
    public static final RawAnimation AIM_FIRING = RawAnimation.begin().then("aim_firing", Animation.LoopType.HOLD_ON_LAST_FRAME);//.thenLoop("aim");
    public static final RawAnimation AIM_RELOAD_START = RawAnimation.begin().then("aim_reload_start", Animation.LoopType.PLAY_ONCE).thenLoop("idle");
    public static final RawAnimation MELEE = RawAnimation.begin().then("melee", Animation.LoopType.HOLD_ON_LAST_FRAME);
    public static final RawAnimation SPRINTING = RawAnimation.begin().thenLoop("sprinting");
}
