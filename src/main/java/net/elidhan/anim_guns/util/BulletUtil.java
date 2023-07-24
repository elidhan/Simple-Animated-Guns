package net.elidhan.anim_guns.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.*;

import java.lang.Math;

public class BulletUtil
{
    public static Vec3d horiSpread(PlayerEntity player, float x_spread)
    {
        Vec3d vec3d = player.getOppositeRotationVector(1.0f);
        AxisAngle4f axisAngle = new AxisAngle4f(x_spread, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z);
        Quaternionf quaternion = new Quaternionf(axisAngle);

        Vec3d vec3d2 = player.getRotationVec(1.0f);

        Vector3f vec3f = new Vector3f((float) vec3d2.x, (float) vec3d2.y, (float) vec3d2.z);
        vec3f.rotate(quaternion);

        return new Vec3d(vec3f.x, vec3f.y, vec3f.z);
    }

    public static Vec3d vertiSpread(PlayerEntity player, float y_spread)
    {
        Vec3d vec3d = getOppositeRotationVector(0, player.getYaw(1.0f) - 90);
        AxisAngle4f axisAngle = new AxisAngle4f(y_spread, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z);
        Quaternionf quaternion = new Quaternionf(axisAngle);

        Vec3d vec3d2 = player.getRotationVec(1.0f);

        Vector3f vec3f = new Vector3f((float) vec3d2.x, (float) vec3d2.y, (float) vec3d2.z);
        vec3f.rotate(quaternion);

        return new Vec3d(vec3f.x, vec3f.y, vec3f.z);
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
