package net.elidhan.anim_guns.item.gun;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.entity.projectile.BulletEntity;
import net.elidhan.anim_guns.util.InventoryUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public abstract class GunTemplateItem extends Item {

    public abstract Item reqAmmo();
    public abstract float reloadCD();
    public abstract int useCD();
    public abstract float dmg();
    public abstract float spread();
    public abstract float recoil();
    public abstract float recoilMult();
    public abstract int clipSize();

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public GunTemplateItem(Settings settings) {
        super(settings);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -4.0f, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack itemStack = user.getStackInHand(hand);

        if (hand == Hand.MAIN_HAND && !user.isSprinting()) {
            if (isLoaded(itemStack)) {

                float kick = user.getPitch() - getRecoil(user);
                user.getItemCooldownManager().set(this, useCD());
                useAmmo(itemStack);

                if(!world.isClient())
                {
                    BulletEntity bullet = new BulletEntity(world, user, dmg());
                    bullet.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 5.0f, spread());
                    world.spawnEntity(bullet);

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeFloat(kick);
                    ServerPlayNetworking.send(((ServerPlayerEntity)user),AnimatedGuns.RECOIL_PACKET_ID, buf);
                }

            } else if (hasReserveAmmo(user, reqAmmo())) {
                user.setCurrentHand(hand);
            }
        }
        return TypedActionResult.fail(itemStack);
    }

    private float getRecoil(PlayerEntity user) {
        return user.isSneaking() ? recoil() * recoilMult() : recoil();
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);

        if (!isLoaded(stack) && remainingUseTicks <= 4) {
            finishReload((PlayerEntity) user, stack);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack itemStack = super.finishUsing(stack, world, user);
        finishReload((PlayerEntity) user, itemStack);

        return itemStack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return (int) reloadCD();
    }

    public static boolean isLoaded(ItemStack stack) {
        return remainingAmmo(stack) > 0;
    }

    private void useAmmo(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        int newAmmo = nbtCompound.getInt("Clip") - 1;

        nbtCompound.putInt("Clip", newAmmo);
    }

    public void finishReload(PlayerEntity player, ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains("maxClip")) nbtCompound.putInt("maxClip", clipSize());

        if (reserveAmmoCount(player, reqAmmo()) > clipSize()) {
            nbtCompound.putInt("Clip", clipSize());
            InventoryUtil.removeItemFromInventory(player, reqAmmo(), clipSize());
        } else {
            nbtCompound.putInt("Clip", reserveAmmoCount(player, reqAmmo()));
            InventoryUtil.removeItemFromInventory(player, reqAmmo(), reserveAmmoCount(player, reqAmmo()));
        }
    }

    private static int remainingAmmo(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        return nbtCompound.getInt("Clip");
    }

    public static boolean hasReserveAmmo(PlayerEntity player, Item item) {
        return InventoryUtil.itemCountInInventory(player, item) > 0;
    }

    public static int reserveAmmoCount(PlayerEntity player, Item item) {
        return InventoryUtil.itemCountInInventory(player, item);
    }
}
