package net.elidhan.anim_guns.item.gun;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.netty.buffer.Unpooled;
import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.AnimatedGunsClient;
import net.elidhan.anim_guns.entity.projectile.BulletEntity;
import net.elidhan.anim_guns.util.InventoryUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public abstract class GunTemplateItem extends Item
{
    protected abstract Item reqAmmo();
    protected abstract float reloadCD();
    protected abstract int reloadStageOne();
    protected abstract int reloadStageTwo();
    protected abstract int reloadStageThree();
    protected abstract SoundEvent reload_p1();
    protected abstract SoundEvent reload_p2();
    protected abstract SoundEvent reload_p3();
    protected abstract SoundEvent shootSound();
    protected abstract int loadingType();
    protected abstract int reloadCycles();
    protected abstract boolean hasScope();
    protected abstract int useCD();
    protected abstract float dmg();
    protected abstract int rps();
    protected abstract float spread();
    protected abstract float recoil();
    protected abstract int clipSize();
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

        nbtCompound.putBoolean("hasScope", hasScope());
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
        if (world.isClient())
        {
            if (((PlayerEntity)entity).getStackInHand(Hand.MAIN_HAND) == stack
                    && AnimatedGunsClient.reloadToggle.isPressed()
                    && remainingAmmo(stack) < clipSize()
                    && reserveAmmoCount(((PlayerEntity) entity), reqAmmo()) > 0
                    && !nbtCompound.getBoolean("isReloading")
            )
            {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeBoolean(true);
                ClientPlayNetworking.send(new Identifier("anim_guns:reload"), buf);
            }
            if (nbtCompound.getBoolean("isReloading")
                    && (((PlayerEntity)entity).getStackInHand(Hand.MAIN_HAND) != stack
                    || (reserveAmmoCount((PlayerEntity) entity, reqAmmo()) <= 0 && nbtCompound.getInt("reloadCycles") <= 1)
                    || (nbtCompound.getInt("reloadTick") >= reloadCD())
                    || (remainingAmmo(stack) >= clipSize() && nbtCompound.getInt("reloadCycles") <= 1))
            )
            {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeBoolean(false);
                ClientPlayNetworking.send(new Identifier("anim_guns:reload"), buf);
            }
        }
        if (nbtCompound.getBoolean("isReloading"))
        {
            doReloadTick(world, nbtCompound, (PlayerEntity)entity, stack);
        }
        else
        {
            if (nbtCompound.getInt("reloadTick") > reloadStageThree() && nbtCompound.getInt("reloadTick") < reloadCD())
                finishReload((PlayerEntity) entity, stack);

            nbtCompound.putInt("reloadTick", 0);
        }
    }
    private void doReloadTick(World world, NbtCompound nbtCompound, PlayerEntity player, ItemStack stack)
    {
        int rTick = nbtCompound.getInt("reloadTick");

        nbtCompound.putInt("reloadTick", nbtCompound.getInt("reloadTick") + 1);

        if(!world.isClient())
        {
            if(rTick == reloadStageOne())
            {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),reload_p1(),SoundCategory.PLAYERS,1.0f, 1.0f);
            }
            else if (rTick == reloadStageTwo())
            {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),reload_p2(),SoundCategory.PLAYERS,1.0f, 1.0f);
            }
            else if (rTick == reloadStageThree()+1)
            {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),reload_p3(),SoundCategory.PLAYERS,1.0f, 1.0f);
            }
        }

        switch(loadingType())
        {
            case 1:
                if(rTick >= reloadCD()
                        && reserveAmmoCount(player, reqAmmo()) > 0)
                {
                    nbtCompound.putInt("currentCycle", 1);
                    finishReload(player, stack);
                    nbtCompound.putInt("reloadTick", 0);
                }
                break;
            case 2:
                if (rTick >= reloadStageThree()
                        && nbtCompound.getInt("currentCycle") < nbtCompound.getInt("reloadCycles")
                        && reserveAmmoCount(player, reqAmmo()) > 0)
                {
                    nbtCompound.putInt("Clip", nbtCompound.getInt("Clip")+1);
                    InventoryUtil.removeItemFromInventory(player, reqAmmo(), 1);
                    if (remainingAmmo(stack) < clipSize() && reserveAmmoCount(player, reqAmmo()) > 0)
                    {
                        nbtCompound.putInt("reloadTick", reloadStageTwo());
                    }
                    nbtCompound.putInt("currentCycle", nbtCompound.getInt("Clip"));
                }
                break;
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
            for(int i = 0; i < rps(); i++)
            {
                BulletEntity bullet = new BulletEntity(world, user, dmg());
                bullet.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 3.0f, spread());
                world.spawnEntity(bullet);
            }
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeFloat(kick);
            ServerPlayNetworking.send(((ServerPlayerEntity) user), AnimatedGuns.RECOIL_PACKET_ID, buf);

            world.playSound(null, user.getX(), user.getY(), user.getZ(),shootSound(),SoundCategory.PLAYERS,1.0f, 1.0f);
        }
    }
    private float getRecoil(PlayerEntity user)
    {
        return user.isSneaking() ? recoil() / 2 : recoil();
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
        }
        else
        {
            int ammoToLoad = nbtCompound.getInt("maxClip") - nbtCompound.getInt("Clip");

            if (reserveAmmoCount(player, reqAmmo()) >= ammoToLoad)
            {
                nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + ammoToLoad);
                InventoryUtil.removeItemFromInventory(player, reqAmmo(), ammoToLoad);
            }
            else
            {
                nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + reserveAmmoCount(player, reqAmmo()));
                InventoryUtil.removeItemFromInventory(player, reqAmmo(), reserveAmmoCount(player, reqAmmo()));
            }
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
