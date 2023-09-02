package net.elidhan.anim_guns.compat;

import net.elidhan.anim_guns.item.GunItem;
import net.projectile_damage.api.IProjectileWeapon;
import net.projectile_damage.api.RangedWeaponKind;

public class ProjectileDamageCompat
{
    static RangedWeaponKind GUN = RangedWeaponKind.custom(1, 1, 1);
    public static void register(GunItem gun)
    {
        ((IProjectileWeapon)gun).setRangedWeaponKind(GUN);
        ((IProjectileWeapon)gun).setProjectileDamage((gun).getGunDamage());
    }
}
