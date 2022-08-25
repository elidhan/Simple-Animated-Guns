package net.elidhan.anim_guns.item;

import io.netty.buffer.Unpooled;
import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.AnimatedGunsClient;
import net.elidhan.anim_guns.entity.projectile.BulletEntity;
import net.elidhan.anim_guns.util.InventoryUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
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

public abstract class GunItem
extends Item
implements FabricItem
{
    private final float gunDamage;
    private final int rateOfFire;
    private final int magSize;
    private final Item ammoType;
    private final int reloadCooldown;
    private final float bulletSpread;
    private final float gunRecoil;
    private final int pelletCount;
    private final int loadingType;
    private final SoundEvent reload1;
    private final SoundEvent reload2;
    private final SoundEvent reload3;
    private final SoundEvent shootSound;
    private final int reloadCycles;
    private final boolean isScoped;
    private final int reloadStage1;
    private final int reloadStage2;
    private final int reloadStage3;
    public GunItem(Settings settings,
                   float gunDamage, int rateOfFire, int magSize,
                   Item ammoType, int reloadCooldown, float bulletSpread,
                   float gunRecoil, int pelletCount, int loadingType,
                   SoundEvent reload1, SoundEvent reload2, SoundEvent reload3,
                   SoundEvent shootSound, int reloadCycles, boolean isScoped,
                   int reloadStage1, int reloadStage2, int reloadStage3)
    {
        super(settings.maxDamage((magSize*10)+1));

        this.gunDamage = gunDamage;
        this.rateOfFire = rateOfFire;
        this.magSize = magSize;
        this.ammoType = ammoType;
        this.reloadCooldown = reloadCooldown;
        this.bulletSpread = bulletSpread;
        this.gunRecoil = gunRecoil;
        this.pelletCount = pelletCount;
        this.loadingType = loadingType;
        this.reload1 = reload1;
        this.reload2 = reload2;
        this.reload3 = reload3;
        this.shootSound = shootSound;
        this.reloadCycles = reloadCycles;
        this.isScoped = isScoped;
        this.reloadStage1 = reloadStage1;
        this.reloadStage2 = reloadStage2;
        this.reloadStage3 = reloadStage3;
    }
    public void setDefaultNBT(NbtCompound nbtCompound)
    {
        nbtCompound.putInt("reloadTick", 0);
        nbtCompound.putInt("currentCycle", 1);
        //nbtCompound.putInt("reloadCycles", this.reloadCycles);

        nbtCompound.putInt("Clip", 0);
        //nbtCompound.putInt("clipSize", this.magSize);

        nbtCompound.putBoolean("isScoped", this.isScoped);
        nbtCompound.putBoolean("isReloading", false);
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        //Just setting the gun's default NBT tags
        //If there's a better way to do this, let me know
        if (!nbtCompound.contains("reloadTick"))
        {
            setDefaultNBT(nbtCompound);
        }
        //This part's for the keypress
        //to reload the gun
        if (world.isClient())
        {
            if (((PlayerEntity)entity).getStackInHand(Hand.MAIN_HAND) == stack
                    && AnimatedGunsClient.reloadToggle.isPressed()
                    && remainingAmmo(stack) < this.magSize
                    && reserveAmmoCount(((PlayerEntity) entity), this.ammoType) > 0
                    && !nbtCompound.getBoolean("isReloading")
            )
            {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeBoolean(true);
                ClientPlayNetworking.send(new Identifier("anim_guns:reload"), buf);
            }
        }
        //I was wondering why my gun kept reloading even after I switch off it
        //Maybe moving this off the if(world.isClient()) check will help
        if (nbtCompound.getBoolean("isReloading")
                && (((PlayerEntity)entity).getStackInHand(Hand.MAIN_HAND) != stack
                || (reserveAmmoCount((PlayerEntity) entity, this.ammoType) <= 0 && this.reloadCycles <= 1)
                || (nbtCompound.getInt("reloadTick") >= this.reloadCooldown)
                || (remainingAmmo(stack) >= this.magSize && this.reloadCycles <= 1))
        )
        {
            nbtCompound.putBoolean("isReloading",false);
            //PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            //buf.writeBoolean(false);
            //ClientPlayNetworking.send(new Identifier("anim_guns:reload"), buf);
        }
        //The actual reload process/tick
        if (nbtCompound.getBoolean("isReloading"))
        {
            doReloadTick(world, nbtCompound, (PlayerEntity)entity, stack);
        }
        else
        {
            if (nbtCompound.getInt("reloadTick") > this.reloadStage3 && nbtCompound.getInt("reloadTick") <= this.reloadCooldown)
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
            if(rTick == this.reloadStage1)
            {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),this.reload1,SoundCategory.MASTER,1.0f, 1.0f);
            }
            else if (rTick == this.reloadStage2)
            {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),this.reload2,SoundCategory.MASTER,1.0f, 1.0f);
            }
            else if (rTick == this.reloadStage3+1)
            {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),this.reload3,SoundCategory.MASTER,1.0f, 1.0f);
            }
        }

        switch(this.loadingType)
        {
            case 1:
                if(rTick >= this.reloadCooldown
                        && reserveAmmoCount(player, this.ammoType) > 0)
                {
                    nbtCompound.putInt("currentCycle", 1);
                    finishReload(player, stack);
                    nbtCompound.putInt("reloadTick", 0);
                }
                break;
            case 2:
                if (rTick >= this.reloadStage3
                        && nbtCompound.getInt("currentCycle") < this.reloadCycles
                        && reserveAmmoCount(player, this.ammoType) > 0)
                {
                    nbtCompound.putInt("Clip", nbtCompound.getInt("Clip")+1);
                    InventoryUtil.removeItemFromInventory(player, this.ammoType, 1);
                    if (remainingAmmo(stack) < this.magSize && reserveAmmoCount(player, this.ammoType) > 0)
                    {
                        nbtCompound.putInt("reloadTick", this.reloadStage2);
                    }
                    nbtCompound.putInt("currentCycle", nbtCompound.getInt("Clip"));
                    stack.setDamage(this.getMaxDamage()-((nbtCompound.getInt("Clip")*10)+1));
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

                if(this.reloadCycles > 1)
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
        user.getItemCooldownManager().set(this, this.rateOfFire);

        if (!world.isClient())
        {
            for(int i = 0; i < this.pelletCount; i++)
            {
                BulletEntity bullet = new BulletEntity(user, world, this.gunDamage);
                bullet.setPos(user.getX(),user.getEyeY(),user.getZ());
                bullet.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 4, this.bulletSpread);
                bullet.setAccel(bullet.getVelocity());

                world.spawnEntity(bullet);
            }
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeFloat(kick);
            ServerPlayNetworking.send(((ServerPlayerEntity) user), AnimatedGuns.RECOIL_PACKET_ID, buf);
        }

        if(!user.getAbilities().creativeMode)
        {
            useAmmo(itemStack);
            itemStack.damage(10, user, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(),shootSound,SoundCategory.MASTER,1.0f, 1.0f);
    }
    private float getRecoil(PlayerEntity user)
    {
        return user.isSneaking() ? this.gunRecoil / 2 : this.gunRecoil;
    }
    public static boolean isLoaded(ItemStack stack)
    {
        return remainingAmmo(stack) > 0;
    }
    private void useAmmo(ItemStack stack)
    {
        NbtCompound nbtCompound = stack.getOrCreateNbt();

        nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") - 1);
    }
    public void finishReload(PlayerEntity player, ItemStack stack)
    {
        NbtCompound nbtCompound = stack.getOrCreateNbt();

        if (nbtCompound.getInt("Clip") <= 0)
        {
            if (reserveAmmoCount(player, this.ammoType) > this.magSize)
            {
                nbtCompound.putInt("Clip", this.magSize);
                InventoryUtil.removeItemFromInventory(player, this.ammoType, this.magSize);
            } else
            {
                nbtCompound.putInt("Clip", reserveAmmoCount(player, this.ammoType));
                InventoryUtil.removeItemFromInventory(player, this.ammoType, reserveAmmoCount(player, this.ammoType));
            }
        }
        else
        {
            int ammoToLoad = this.magSize - nbtCompound.getInt("Clip");

            if (reserveAmmoCount(player, this.ammoType) >= ammoToLoad)
            {
                nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + ammoToLoad);
                InventoryUtil.removeItemFromInventory(player, this.ammoType, ammoToLoad);
            }
            else
            {
                nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + reserveAmmoCount(player, this.ammoType));
                InventoryUtil.removeItemFromInventory(player, this.ammoType, reserveAmmoCount(player, this.ammoType));
            }
        }

        stack.setDamage(this.getMaxDamage()-((nbtCompound.getInt("Clip")*10)+1));
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

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack)
    {
        return false;
    }
}
