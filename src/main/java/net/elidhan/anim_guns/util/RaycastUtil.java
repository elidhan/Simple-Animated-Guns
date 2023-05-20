package net.elidhan.anim_guns.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class RaycastUtil
{
    public static Vec3d horiSpread(PlayerEntity user, float x_spread)
    {
        Vec3d vec3d = user.getOppositeRotationVector(1.0f);
        Quaternion quaternion = new Quaternion(new Vec3f(vec3d), x_spread, true);
        Vec3d vec3d2 = user.getRotationVec(1.0f);
        Vec3f vec3f = new Vec3f(vec3d2);
        vec3f.rotate(quaternion);

        return new Vec3d(vec3f);
    }
}
