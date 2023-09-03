package net.elidhan.anim_guns.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class BulletUtil
{
    public static Vec3d horiSpread(PlayerEntity player, float x_spread)
    {
        Vec3d vec3d = player.getOppositeRotationVector(1.0f);
        Quaternion quaternion = new Quaternion(new Vec3f(vec3d), x_spread, true);
        Vec3d vec3d2 = player.getRotationVec(1.0f);
        Vec3f vec3f = new Vec3f(vec3d2);
        vec3f.rotate(quaternion);

        return new Vec3d(vec3f);
    }
    public static Vec3d vertiSpread(PlayerEntity player, float y_spread)
    {
        Vec3d vec3d = getOppositeRotationVector(0,player.getYaw(1.0f)-90);

        Quaternion quaternion = new Quaternion(new Vec3f(vec3d), y_spread, true);
        Vec3d vec3d2 = player.getRotationVec(1.0f);
        Vec3f vec3f = new Vec3f(vec3d2);
        vec3f.rotate(quaternion);

        return new Vec3d(vec3f);
    }

    protected static Vec3d getOppositeRotationVector(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180);
        float g = -yaw * ((float)Math.PI / 180);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

}