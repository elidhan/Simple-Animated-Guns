package net.elidhan.anim_guns.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.*;

import java.lang.Math;

public class BulletUtil
{
    public static Vec3d horiSpread(PlayerEntity player, float x_spread)
    {
        Vec3d vec3d = player.getOppositeRotationVector(1.0F);
        Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(x_spread * 0.017453292F, vec3d.x, vec3d.y, vec3d.z);
        Vec3d vec3d2 = player.getRotationVec(1.0F);

        Vector3f vector3f = vec3d2.toVector3f().rotate(quaternionf);

        return new Vec3d(vector3f.x, vector3f.y, vector3f.z);
    }

    public static Vec3d vertiSpread(PlayerEntity player, float y_spread)
    {
        Vec3d vec3d = getOppositeRotationVector(0, player.getYaw(1.0f) - 90);
        Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(y_spread * 0.017453292F, vec3d.x, vec3d.y, vec3d.z);
        Vec3d vec3d2 = player.getRotationVec(1.0F);

        Vector3f vector3f = vec3d2.toVector3f().rotate(quaternionf);

        return new Vec3d(vector3f.x, vector3f.y, vector3f.z);
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
