package net.elidhan.anim_guns.item.gun;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.AnimatedGunsClient;
import net.elidhan.anim_guns.entity.projectile.BulletEntity;
import net.elidhan.anim_guns.util.InventoryUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public abstract class GunTemplateItem extends Item
{

    public abstract Item reqAmmo();

    public abstract float reloadCD();

    public abstract int reloadStageOne();

    public abstract int reloadStageTwo();

    public abstract int reloadStageThree();

    public abstract int reloadCycles();

    public abstract int useCD();

    public abstract float dmg();

    public abstract float spread();

    public abstract float recoil();

    public abstract float recoilMult();

    public abstract int clipSize();

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public GunTemplateItem(Settings settings)
    {
        super(settings);

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -4.0f, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot)
    {
        if (slot == EquipmentSlot.MAINHAND)
        {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }

    public void setDefaultNBT(NbtCompound nbtCompound)
    {
        nbtCompound.putInt("reloadTick", 0);
        nbtCompound.putInt("currentCycle", 1);
        nbtCompound.putInt("reloadCycles", reloadCycles());

        nbtCompound.putInt("Clip", 0);
        nbtCompound.putInt("maxClip", clipSize());

        nbtCompound.putBoolean("isReloading", false);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains("isReloading"))
        {
            setDefaultNBT(nbtCompound);
        }

        if (((PlayerEntity)entity).getStackInHand(Hand.MAIN_HAND) == stack && AnimatedGunsClient.reloadToggle.isPressed() && remainingAmmo(stack) < clipSize() && reserveAmmoCount(((PlayerEntity) entity), reqAmmo()) > 0)
        {
            nbtCompound.putBoolean("isReloading", true);
        }
        else if (((PlayerEntity)entity).getStackInHand(Hand.MAIN_HAND) != stack
                || (reserveAmmoCount((PlayerEntity) entity, reqAmmo()) <= 0 && nbtCompound.getInt("reloadCycles") <= 1)
                || (nbtCompound.getInt("reloadTick") >= reloadCD())
                || (remainingAmmo(stack) >= clipSize() && nbtCompound.getInt("reloadCycles") <= 1)
        )
        {
            nbtCompound.putBoolean("isReloading", false);
        }

        if (nbtCompound.getBoolean("isReloading"))
        {
            doReloadTick(nbtCompound, (PlayerEntity)entity, stack);
        }
        else
        {
            if (nbtCompound.getInt("reloadTick") > reloadStageThree() && nbtCompound.getInt("reloadTick") < reloadCD())
                finishReload((PlayerEntity) entity, stack);

            nbtCompound.putInt("reloadTick", 0);
        }
    }

    private void doReloadTick(NbtCompound nbtCompound, PlayerEntity entity, ItemStack stack)
    {
        nbtCompound.putInt("reloadTick", nbtCompound.getInt("reloadTick") + 1);

        if (nbtCompound.getInt("reloadTick") >= reloadStageThree())
        {
            //If cycle is last or not
            if (nbtCompound.getInt("currentCycle") < nbtCompound.getInt("reloadCycles") && reserveAmmoCount(entity, reqAmmo()) > 0)
            {
                nbtCompound.putInt("Clip", nbtCompound.getInt("Clip")+1);
                InventoryUtil.removeItemFromInventory(entity, reqAmmo(), 1);

                if (remainingAmmo(stack) < clipSize() && reserveAmmoCount(entity, reqAmmo()) > 0)
                {
                    nbtCompound.putInt("reloadTick", reloadStageTwo());
                }

                nbtCompound.putInt("currentCycle", nbtCompound.getInt("Clip"));
            }
            else
            {
                //If single loader or not
                if(nbtCompound.getInt("reloadCycles") > 1)
                {
                    nbtCompound.putInt("currentCycle", nbtCompound.getInt("Clip"));
                }
                else
                {
                    nbtCompound.putInt("currentCycle", 1);
                    finishReload(entity, stack);
                    nbtCompound.putInt("reloadTick", 0);
                }
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack itemStack = user.getStackInHand(hand);

        if (hand == Hand.MAIN_HAND && !user.isSprinting())
        {
            if (isLoaded(itemStack))
            {
                shoot(world, user, itemStack);

                if(itemStack.getOrCreateNbt().getInt("reloadCycles") > 1)
                {
                    itemStack.getOrCreateNbt().putInt("currentCycle", itemStack.getOrCreateNbt().getInt("Clip"));
                }

                itemStack.getOrCreateNbt().putInt("reloadTick",0);
                itemStack.getOrCreateNbt().putBoolean("isReloading",false);
            }
        }
        return TypedActionResult.fail(itemStack);
    }

    public void shoot(World world, PlayerEntity user, ItemStack itemStack)
    {
        float kick = user.getPitch() - getRecoil(user);
        user.getItemCooldownManager().set(this, useCD());
        useAmmo(itemStack);

        if (!world.isClient())
        {
            BulletEntity bullet = new BulletEntity(world, user, dmg());
            bullet.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 5.0f, spread());
            world.spawnEntity(bullet);

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeFloat(kick);
            ServerPlayNetworking.send(((ServerPlayerEntity) user), AnimatedGuns.RECOIL_PACKET_ID, buf);
        }
    }

    private float getRecoil(PlayerEntity user)
    {
        return user.isSneaking() ? recoil() * recoilMult() : recoil();
    }

    @Override
    public int getMaxUseTime(ItemStack stack)
    {
        return (int) reloadCD();
    }

    public static boolean isLoaded(ItemStack stack)
    {
        return remainingAmmo(stack) > 0;
    }

    private void useAmmo(ItemStack stack)
    {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        int newAmmo = nbtCompound.getInt("Clip") - 1;

        nbtCompound.putInt("Clip", newAmmo);
    }

    public void finishReload(PlayerEntity player, ItemStack stack)
    {
        NbtCompound nbtCompound = stack.getOrCreateNbt();

        if (nbtCompound.getInt("Clip") <= 0)
        {
            if (reserveAmmoCount(player, reqAmmo()) > clipSize())
            {
                nbtCompound.putInt("Clip", clipSize());
                InventoryUtil.removeItemFromInventory(player, reqAmmo(), clipSize());
            } else
            {
                nbtCompound.putInt("Clip", reserveAmmoCount(player, reqAmmo()));
                InventoryUtil.removeItemFromInventory(player, reqAmmo(), reserveAmmoCount(player, reqAmmo()));
            }
        } else
        {
            int ammoToLoad = nbtCompound.getInt("maxClip") - nbtCompound.getInt("Clip");

            if (reserveAmmoCount(player, reqAmmo()) >= ammoToLoad)
            {
                nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + ammoToLoad);
                InventoryUtil.removeItemFromInventory(player, reqAmmo(), ammoToLoad);
            } else
            {
                nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + reserveAmmoCount(player, reqAmmo()));
                InventoryUtil.removeItemFromInventory(player, reqAmmo(), reserveAmmoCount(player, reqAmmo()));
            }
        }

        if ((int) reloadCD() - nbtCompound.getInt("reloadTick") > 0)
        {
            player.sendMessage(new LiteralText(Integer.toString((int) reloadCD() - nbtCompound.getInt("reloadTick"))), false);
            player.getItemCooldownManager().set(this, (int) reloadCD() - nbtCompound.getInt("reloadTick"));
        }
    }

    private static int remainingAmmo(ItemStack stack)
    {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        return nbtCompound.getInt("Clip");
    }

    public static int reserveAmmoCount(PlayerEntity player, Item item)
    {
        return InventoryUtil.itemCountInInventory(player, item);
    }
}
