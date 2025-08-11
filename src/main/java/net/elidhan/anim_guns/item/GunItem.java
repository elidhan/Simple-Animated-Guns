package net.elidhan.anim_guns.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.SingletonGeoAnimatable;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.azurelib.cache.AnimatableIdCache;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.keyframe.event.SoundKeyframeEvent;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.AnimatedGunsClient;
import net.elidhan.anim_guns.animations.GunAnimations;
import net.elidhan.anim_guns.attribute.GunAttributes;
import net.elidhan.anim_guns.client.render.GunRenderer;
import net.elidhan.anim_guns.entity.projectile.BulletProjectileEntity;
import net.elidhan.anim_guns.util.BulletUtil;
import net.elidhan.anim_guns.util.InventoryUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class GunItem extends RangedWeaponItem implements FabricItem, GeoAnimatable, GeoItem
{
    protected static final UUID BULLET_DAMAGE_MODIFIER_ID = UUID.fromString("49bfefca-1adb-4530-90de-c52dded51144");
    public final Random random;
    private final String gunID;
    private final String animationID;
    private final float gunDamage;
    private final int rateOfFire;
    private final int magSize;
    public final Item ammoType;
    private final int reloadCooldown;
    private final float[] bulletSpread;
    private final float[] gunRecoil;
    private final int pelletCount;
    private final LoadingType loadingType;
    private final SoundEvent reloadSoundStart;
    private final SoundEvent reloadSoundMagOut;
    private final SoundEvent reloadSoundMagIn;
    private final SoundEvent reloadSoundEnd;
    private final SoundEvent shootSound;
    private final SoundEvent postShootSound;
    private final int reloadCycles;
    private final boolean isScoped;
    private final boolean unscopeAfterShot;
    private final int reloadStage1;
    private final int reloadStage2;
    private final int reloadStage3;
    public final FiringType firingType;
    public final ArmType armType;
    private final AttachmentItem.AttachType[] acceptedAttachmentTypes;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    protected final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
    protected final AnimatableInstanceCache animationCache = AzureLibUtil.createInstanceCache(this);

    public GunItem(Settings settings, String gunID, String animationID,
                   float gunDamage, int rateOfFire, int magSize,
                   Item ammoType, int reloadCooldown, float[] bulletSpread,
                   float[] gunRecoil, int pelletCount, LoadingType loadingType,
                   SoundEvent reloadSoundStart, SoundEvent reloadSoundMagOut, SoundEvent reloadSoundMagIn, SoundEvent reloadSoundEnd,
                   SoundEvent shootSound, SoundEvent postShootSound, int reloadCycles, boolean isScoped, boolean unscopeAfterShot,
                   int reloadStage1, int reloadStage2, int reloadStage3, FiringType firingType, ArmType armType, AttachmentItem.AttachType[] acceptedAttachmentTypes)
    {
        super(settings.maxDamage((magSize * 10) + 1));
        SingletonGeoAnimatable.registerSyncedAnimatable(this);

        this.random = new Random();
        this.gunID = gunID;
        this.animationID = animationID;
        this.gunDamage = gunDamage;
        this.rateOfFire = rateOfFire;
        this.magSize = magSize;
        this.ammoType = ammoType;
        this.reloadCooldown = reloadCooldown;
        this.bulletSpread = bulletSpread;
        this.gunRecoil = gunRecoil;
        this.pelletCount = pelletCount;
        this.loadingType = loadingType;
        this.reloadSoundStart = reloadSoundStart;
        this.reloadSoundMagOut = reloadSoundMagOut;
        this.reloadSoundMagIn = reloadSoundMagIn;
        this.reloadSoundEnd = reloadSoundEnd;
        this.shootSound = shootSound;
        this.postShootSound = postShootSound;
        this.reloadCycles = reloadCycles;
        this.isScoped = isScoped;
        this.unscopeAfterShot = unscopeAfterShot;
        this.reloadStage1 = reloadStage1;
        this.reloadStage2 = reloadStage2;
        this.reloadStage3 = reloadStage3;
        this.firingType = firingType;
        this.armType = armType;
        this.acceptedAttachmentTypes = acceptedAttachmentTypes;
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(GunAttributes.GUN_DAMAGE, new EntityAttributeModifier(BULLET_DAMAGE_MODIFIER_ID, "Weapon modifier", this.gunDamage, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack stack = user.getStackInHand(hand);

        if (hand == Hand.MAIN_HAND)
        {
            if (!user.getItemCooldownManager().isCoolingDown(this) && !user.isSprinting() && isLoaded(stack))
            {
                this.shoot(world, user, stack);
                user.getItemCooldownManager().set(this, this.rateOfFire);
            }
        }
        return TypedActionResult.fail(stack);
    }

    public void shoot(World world, PlayerEntity user, ItemStack itemStack)
    {
        itemStack.getOrCreateNbt().putInt("reloadTick", 0);
        itemStack.getOrCreateNbt().putBoolean("isReloading", false);

        double h_kick = getRecoilX(itemStack);
        float v_kick = getRecoilY(itemStack);

        if (shouldUnscopeAfterShot() && itemStack.getOrCreateNbt().getBoolean("isAiming"))
        {
            itemStack.getOrCreateNbt().putBoolean("isAiming", false);
        }

        if (!world.isClient())
        {
            for (int i = 0; i < this.pelletCount; i++)
            {
                BulletProjectileEntity bullet = new BulletProjectileEntity(user, world, (float)user.getAttributeValue(GunAttributes.GUN_DAMAGE), this.pelletCount);

                bullet.setPosition(user.getX(), user.getEyeY(), user.getZ());

                Vec3d vertiSpread = BulletUtil.vertiSpread(user, (random.nextFloat(-bulletSpread[0] * 5, bulletSpread[0] * 5)) * getSpreadMult(itemStack));
                Vec3d horiSpread = BulletUtil.horiSpread(user, (random.nextFloat(-bulletSpread[1] * 5, bulletSpread[1] * 5))  * getSpreadMult(itemStack));

                Vec3d result = user.getRotationVector().add(vertiSpread).add(horiSpread);

                bullet.setVelocity(result.getX(), result.getY(), result.getZ(), 20, 0);
                bullet.setBaseVel(bullet.getVelocity());
                bullet.setOwner(user);

                world.spawnEntity(bullet);
            }

            //animation
            final long id = GeoItem.getOrAssignId(itemStack, (ServerWorld) world);

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(itemStack.getOrCreateNbt().getBoolean("isAiming") ? "aim_firing" : "firing");
            buf.writeLong(id);
            ServerPlayNetworking.send(((ServerPlayerEntity) user), AnimatedGuns.PLAY_ANIMATION_PACKET_ID, buf);

            //recoil
            PacketByteBuf buf2 = PacketByteBufs.create();
            buf2.writeFloat(v_kick);
            buf2.writeDouble(h_kick);
            ServerPlayNetworking.send(((ServerPlayerEntity) user), AnimatedGuns.RECOIL_PACKET_ID, buf2);
        }

        if (!user.getAbilities().creativeMode)
        {
            itemStack.getOrCreateNbt().putInt("Clip", itemStack.getOrCreateNbt().getInt("Clip") - 1);
            itemStack.setDamage(this.getMaxDamage() - ((itemStack.getOrCreateNbt().getInt("Clip") * 10) + 1));
        }

        world.playSound(null,
                user.getX(),
                user.getY(),
                user.getZ(),
                this.shootSound, SoundCategory.MASTER, 1.0f, 1.0f);

        if (this.reloadCycles > 1)
        {
            itemStack.getOrCreateNbt().putInt("currentCycle", itemStack.getOrCreateNbt().getInt("Clip"));
        }
    }

    private void setDefaultNBT(ItemStack stack)
    {
        NbtCompound nbtCompound = stack.getOrCreateNbt();

        nbtCompound.putInt("reloadTick", 0);
        nbtCompound.putInt("currentCycle", 1);
        nbtCompound.putInt("Clip", 0);

        nbtCompound.putBoolean("isScoped", this.isScoped);
        nbtCompound.putBoolean("isReloading", false);
        nbtCompound.putBoolean("isAiming", false);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player)
    {
        setDefaultNBT(stack);

        if (world instanceof ServerWorld)
        {
            final long id = GeoItem.getOrAssignId(stack, (ServerWorld) world);
            triggerAnim(player, id, "controller", "idle");
        }
        super.onCraft(stack, world, player);
    }

    // @Override
    // public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    // {
    //     if (Screen.hasShiftDown())
    //     {
    //         tooltip.add(Text.translatable("Ammo: " + (stack.getOrCreateNbt().getInt("Clip")) + "/" + this.magSize).formatted(Formatting.WHITE));
    //         tooltip.add(Text.translatable("Damage: " + this.gunDamage).formatted(Formatting.GRAY));
    //         tooltip.add(Text.translatable("Recoil: " + Math.sqrt((this.gunRecoil[0]*this.gunRecoil[0])+(this.gunRecoil[1]*this.gunRecoil[1]))).formatted(Formatting.GRAY));
    //         tooltip.add(Text.translatable("RPM: " + (int) (((float) 20 / this.rateOfFire) * 60)).formatted(Formatting.GRAY));
    //         tooltip.add(Text.translatable("Reload Time: " + (float) this.reloadCooldown / 20 + "s").formatted(Formatting.GRAY));
    //         tooltip.add(Text.translatable("Uses:").formatted(Formatting.GRAY));
    //         tooltip.add(Text.translatable(this.ammoType.getTranslationKey()).formatted(Formatting.YELLOW));
    //     }
    //     else
    //     {
    //         tooltip.add(Text.translatable("Ammo: " + (stack.getOrCreateNbt().getInt("Clip")) + "/" + this.magSize).formatted(Formatting.WHITE));
    //         tooltip.add(Text.translatable("Uses:").formatted(Formatting.GRAY));
    //         tooltip.add(Text.translatable(this.ammoType.getTranslationKey()).formatted(Formatting.YELLOW));
    //
    //         tooltip.add(Text.translatable("Press Shift to see stats").formatted(Formatting.AQUA));
    //     }
    //     super.appendTooltip(stack, world, tooltip, context);
    // }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        if (Screen.hasShiftDown())
        {
            int ammo = stack.getOrCreateNbt().getInt("Clip");
            tooltip.add(Text.translatable("tooltip.anim_guns.ammo", ammo, this.magSize).formatted(Formatting.WHITE));
            tooltip.add(Text.translatable("tooltip.anim_guns.damage", this.gunDamage).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.anim_guns.recoil", String.format("%.1f", Math.sqrt((this.gunRecoil[0]*this.gunRecoil[0])+(this.gunRecoil[1]*this.gunRecoil[1])))).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.anim_guns.rpm", (int) (((float) 20 / this.rateOfFire) * 60)).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.anim_guns.reload_time", (float) this.reloadCooldown / 20).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.anim_guns.uses").formatted(Formatting.GRAY));
            tooltip.add(Text.translatable(this.ammoType.getTranslationKey()).formatted(Formatting.YELLOW));
        }
        else
        {
            int ammo = stack.getOrCreateNbt().getInt("Clip");
            tooltip.add(Text.translatable("tooltip.anim_guns.ammo", ammo, this.magSize).formatted(Formatting.WHITE));
            tooltip.add(Text.translatable("tooltip.anim_guns.uses").formatted(Formatting.GRAY));
            tooltip.add(Text.translatable(this.ammoType.getTranslationKey()).formatted(Formatting.YELLOW));

            tooltip.add(Text.translatable("tooltip.anim_guns.press_shift").formatted(Formatting.AQUA));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        boolean isSprinting = entity.isSprinting();
        ItemStack mainHandGun = ((PlayerEntity) entity).getMainHandStack();

        if(!nbtCompound.contains(ID_NBT_KEY, NbtElement.NUMBER_TYPE) && world instanceof ServerWorld)
        {
            nbtCompound.putLong(ID_NBT_KEY, AnimatableIdCache.getFreeId((ServerWorld) world));
        }

        final long id = GeoItem.getId(stack);
        AnimationController<GeoAnimatable> animationController = getAnimatableInstanceCache().getManagerForId(id).getAnimationControllers().get("controller");

        if (world.isClient())
        {
            if (animationController.getCurrentAnimation() != null && !animationController.getAnimationState().equals(AnimationController.State.STOPPED))
            {
                if (isSprinting
                        && !mainHandGun.getOrCreateNbt().getBoolean("isAiming")
                        && mainHandGun == stack
                        && !mainHandGun.getOrCreateNbt().getBoolean("isReloading")
                        && !(animationController.isPlayingTriggeredAnimation() && animationController.getCurrentAnimation().animation().name().equals("sprinting"))
                        && (!animationController.getCurrentAnimation().animation().name().equals("melee") || animationController.getAnimationState().equals(AnimationController.State.PAUSED)))
                {
                    animationController.tryTriggerAnimation("sprinting");
                }
                else if ((!isSprinting || mainHandGun != stack) && (animationController.isPlayingTriggeredAnimation() && animationController.getCurrentAnimation().animation().name().equals("sprinting")) && (!animationController.getCurrentAnimation().animation().name().equals("melee") || animationController.getAnimationState().equals(AnimationController.State.PAUSED)))
                {
                    animationController.tryTriggerAnimation(!mainHandGun.getOrCreateNbt().getBoolean("isAiming") ? "idle" : "aim");
                }
            }
            /*
            removed '&& !isSprinting' to allow the player to reload while sprinting.
            original code:
            if (mainHandGun == stack
                    && AnimatedGunsClient.reloadToggle.isPressed()
                    && remainingAmmo(stack) < this.magSize
                    && reserveAmmoCount(((PlayerEntity) entity), this.ammoType) > 0
                    && !nbtCompound.getBoolean("isReloading")
                    && !isSprinting)
             */
            if (mainHandGun == stack
                    && AnimatedGunsClient.reloadToggle.isPressed()
                    && remainingAmmo(stack) < this.magSize
                    && reserveAmmoCount(((PlayerEntity) entity), this.ammoType) > 0
                    && !nbtCompound.getBoolean("isReloading")
                    )
            {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(true);
                ClientPlayNetworking.send(AnimatedGuns.RELOAD_PACKET_ID, buf);
            }
        }

        if(mainHandGun != stack && nbtCompound.getBoolean("isAiming"))
        {
            animationController.tryTriggerAnimation("idle");
            nbtCompound.putBoolean("isAiming", false);
        }

        // The actual reload process/tick
        // removed '&& !isSprinting' to ensure that triggering sprint during a reload no longer interrupts the reload.
        // original code: if (nbtCompound.getBoolean("isReloading") && !isSprinting)
        if (nbtCompound.getBoolean("isReloading"))
        {
            if ((mainHandGun != stack
                    || (reserveAmmoCount((PlayerEntity) entity, this.ammoType) <= 0 && this.reloadCycles <= 1)
                    || (nbtCompound.getInt("reloadTick") >= this.reloadCooldown)
                    || (remainingAmmo(stack) >= this.magSize && this.reloadCycles <= 1)))
                nbtCompound.putBoolean("isReloading", false);

            this.doReloadTick(world, nbtCompound, (PlayerEntity) entity, stack);
        }
        else
        {
            if (nbtCompound.getInt("reloadTick") > this.reloadStage3 && nbtCompound.getInt("reloadTick") <= this.reloadCooldown)
                finishReload((PlayerEntity) entity, stack);

            nbtCompound.putBoolean("isReloading", false);
            nbtCompound.putInt("reloadTick", 0);
        }
    }

    private void doReloadTick(World world, NbtCompound nbtCompound, PlayerEntity player, ItemStack stack)
    {
        int rTick = nbtCompound.getInt("reloadTick");

        if (world instanceof ServerWorld)
        {
            final long id = GeoItem.getOrAssignId(stack, (ServerWorld) world);

            if (nbtCompound.getInt("reloadTick") == this.reloadStage1)
            {
                triggerAnim(player, id, "controller", "reload_magout");
            }
            else if (nbtCompound.getInt("reloadTick") == this.reloadStage2)
            {
                triggerAnim(player, id, "controller", "reload_magin");
            }
            else if (nbtCompound.getInt("reloadTick") == this.reloadStage3)
            {
                triggerAnim(player, id, "controller", "reload_end");
            }
        }

        nbtCompound.putInt("reloadTick", nbtCompound.getInt("reloadTick") + 1);

        switch (this.loadingType)
        {
            case MAGAZINE ->
            {
                if (rTick >= this.reloadCooldown
                        && reserveAmmoCount(player, this.ammoType) > 0)
                {
                    nbtCompound.putInt("currentCycle", 1);
                    finishReload(player, stack);
                    nbtCompound.putInt("reloadTick", 0);
                }
            }
            case PER_CARTRIDGE ->
            {
                if (rTick >= this.reloadStage3
                        && nbtCompound.getInt("currentCycle") < this.reloadCycles
                        && reserveAmmoCount(player, this.ammoType) > 0)
                {
                    nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + 1);
                    InventoryUtil.removeItemFromInventory(player, this.ammoType, 1);
                    if (remainingAmmo(stack) < this.magSize && reserveAmmoCount(player, this.ammoType) > 0)
                    {
                        nbtCompound.putInt("reloadTick", this.reloadStage2);
                    }
                    nbtCompound.putInt("currentCycle", nbtCompound.getInt("Clip"));
                    stack.setDamage(this.getMaxDamage() - ((nbtCompound.getInt("Clip") * 10) + 1));
                }
            }
        }
    }

    public float getRecoilX(ItemStack stack)
    {
        boolean rd = this.random.nextBoolean();
        return (stack.getOrCreateNbt().getBoolean("isAiming") ?
                (rd ? this.gunRecoil[0] : -this.gunRecoil[0]) / 2 :
                (rd ? this.gunRecoil[0] : -this.gunRecoil[0])) * getRecoilMult(stack);
    }

    public float getRecoilY(ItemStack stack)
    {
        return (stack.getOrCreateNbt().getBoolean("isAiming") ? this.gunRecoil[1] / 2 : this.gunRecoil[1]) * getRecoilMult(stack);
    }

    public float getRecoilMult(ItemStack stack)
    {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        NbtList list = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
        //List<AttachmentItem> attachments = new ArrayList<>();
        float recoilMult = 1.0f;

        List<NbtCompound> l = list.stream().filter(NbtCompound.class::isInstance).map(NbtCompound.class::cast).toList();

        for (NbtCompound n : l)
        {
            recoilMult *= ((AttachmentItem) ItemStack.fromNbt(n).getItem()).getRecoilMult();
        }

        return recoilMult;
    }

    public float getSpreadMult(ItemStack stack)
    {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        NbtList list = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
        //List<AttachmentItem> attachments = new ArrayList<>();
        float spreadMult = 1.0f;

        List<NbtCompound> l = list.stream().filter(NbtCompound.class::isInstance).map(NbtCompound.class::cast).toList();

        for (NbtCompound n : l)
        {
            spreadMult *= ((AttachmentItem) ItemStack.fromNbt(n).getItem()).getSpreadMult();
        }

        return spreadMult;
    }

    public static boolean isLoaded(ItemStack stack)
    {
        return remainingAmmo(stack) > 0;
    }

    public void finishReload(PlayerEntity player, ItemStack stack)
    {
        NbtCompound nbtCompound = stack.getOrCreateNbt();

        int ammoToLoad = this.magSize - nbtCompound.getInt("Clip");

        if (reserveAmmoCount(player, this.ammoType) >= ammoToLoad)
        {
            nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + ammoToLoad);
            InventoryUtil.removeItemFromInventory(player, this.ammoType, ammoToLoad);
        } else
        {
            nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + reserveAmmoCount(player, this.ammoType));
            InventoryUtil.removeItemFromInventory(player, this.ammoType, reserveAmmoCount(player, this.ammoType));
        }

        stack.setDamage(this.getMaxDamage() - ((nbtCompound.getInt("Clip") * 10) + 1));
    }

    public void aimAnimation(ItemStack stack, boolean aim, ServerWorld world, PlayerEntity player)
    {
        stack.getOrCreateNbt().putBoolean("isAiming", aim);
        stack.getOrCreateNbt().putBoolean("isScoped", this.isScoped);

        final long id = GeoItem.getOrAssignId(stack, world);
        triggerAnim(player, id, "controller", aim ? "aim":"idle");
    }

    public void toggleSprint(ItemStack stack, boolean sprint, ServerWorld world, PlayerEntity player)
    {
        final long id = GeoItem.getOrAssignId(stack, world);

        if (stack.getOrCreateNbt().getBoolean("isReloading"))
            return;

        if (sprint)
        {
            triggerAnim(player, id, "controller", "sprinting");
        }
    }

    public static int remainingAmmo(ItemStack stack)
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

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner)
    {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        return false;
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

    @Override
    public int getItemBarColor(ItemStack stack)
    {
        return MathHelper.packRgb(0.0f, 1f, 1f);
    }

    public int getRateOfFire()
    {
        return this.rateOfFire;
    }

    public boolean shouldUnscopeAfterShot()
    {
        return this.unscopeAfterShot;
    }

    public String getID()
    {
        return this.gunID;
    }

    public String getAnimationID()
    {
        return this.animationID;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer)
    {
        consumer.accept(new RenderProvider()
        {
            private final GunRenderer renderer = new GunRenderer(new Identifier(AnimatedGuns.MOD_ID, gunID));

            @Override
            public BuiltinModelItemRenderer getCustomRenderer()
            {
                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider()
    {
        return renderProvider;
    }

    private PlayState predicate(AnimationState<GunItem> event)
    {
        if (event.getController().getCurrentAnimation() == null || event.getController().getAnimationState() == AnimationController.State.STOPPED)
        {
            event.getController().tryTriggerAnimation("idle");
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        AnimationController<GunItem> controller = new AnimationController<>(this, "controller", 1, this::predicate)
                .triggerableAnim("idle", GunAnimations.IDLE)
                .triggerableAnim("firing", GunAnimations.FIRING)
                .triggerableAnim("reload_start", GunAnimations.RELOAD_START)
                .triggerableAnim("reload_magout", GunAnimations.RELOAD_MAGOUT)
                .triggerableAnim("reload_magin", GunAnimations.RELOAD_MAGIN)
                .triggerableAnim("reload_end", GunAnimations.RELOAD_END)
                .triggerableAnim("aim", GunAnimations.AIM)
                .triggerableAnim("aim_firing", GunAnimations.AIM_FIRING)
                .triggerableAnim("aim_reload_start", GunAnimations.AIM_RELOAD_START)
                .triggerableAnim("melee", GunAnimations.MELEE)
                .triggerableAnim("sprinting", GunAnimations.SPRINTING);

        controller.setSoundKeyframeHandler(this::soundListener);
        controllers.add(controller);
    }

    /**
     * Handles the sound events for the gun
     *
     * @param gunItemSoundKeyframeEvent the event provided by AzureLib
     */
    private void soundListener(SoundKeyframeEvent<GunItem> gunItemSoundKeyframeEvent)
    {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null)
        {
            switch (gunItemSoundKeyframeEvent.getKeyframeData().getSound())
            {
                case "reload_start" -> player.playSound(this.reloadSoundStart, SoundCategory.PLAYERS, 1, 1);
                case "reload_magout" -> player.playSound(this.reloadSoundMagOut, SoundCategory.PLAYERS, 1, 1);
                case "reload_magin" -> player.playSound(this.reloadSoundMagIn, SoundCategory.PLAYERS, 1, 1);
                case "reload_end" -> player.playSound(this.reloadSoundEnd, SoundCategory.PLAYERS, 1, 1);
                case "post_shoot" -> player.playSound(this.postShootSound, SoundCategory.PLAYERS, 1, 1);
                case "melee" -> player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, 1, 1);
            }
        }
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference)
    {
        if (clickType != ClickType.RIGHT) return false;

        if (otherStack.isEmpty())
        {
            removeFirstStack(stack).ifPresent(itemStack ->
            {
                player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8f, 0.8f + player.getWorld().getRandom().nextFloat() * 0.4f);
                cursorStackReference.set(itemStack);
            });
        }
        else
        {
            int i = addAttachment(stack, otherStack);

            if (i > 0)
            {
                otherStack.decrement(i);
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8f, 0.8f + player.getWorld().getRandom().nextFloat() * 0.4f);
            }

        }

        return true;
    }

    private int addAttachment(ItemStack gun, ItemStack attachment)
    {
        if (attachment.isEmpty() || !(attachment.getItem() instanceof AttachmentItem)) return 0;

        NbtCompound nbtCompound = gun.getOrCreateNbt();

        if (!nbtCompound.contains("Items")) nbtCompound.put("Items", new NbtList());

        int i = getExistingAttachments(gun);int k = Math.min(attachment.getCount(), (3 - i));

        if (k == 0) return 0;

        NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
        Optional<NbtCompound> existingAttach = checkExistingAttachType(attachment, nbtList);

        if (existingAttach.isPresent() || !acceptedAttachment(((AttachmentItem)attachment.getItem()).getAttachType()))
        {
            return 0;
        }
        else
        {
            int attachID = ((AttachmentItem)attachment.getItem()).getId();
            AttachmentItem.AttachType attachType = ((AttachmentItem) attachment.getItem()).getAttachType();

            switch(attachType)
            {
                case SIGHT -> nbtCompound.putInt("sightID", attachID);
                case GRIP -> nbtCompound.putInt("gripID", attachID);
                case MUZZLE -> nbtCompound.putInt("muzzleID", attachID);
            }

            ItemStack itemStack2 = attachment.copyWithCount(k);
            NbtCompound nbtCompound3 = new NbtCompound();
            itemStack2.writeNbt(nbtCompound3);
            nbtList.add(0, nbtCompound3);
        }

        return 1;
    }

    private Optional<ItemStack> removeFirstStack(ItemStack gun)
    {
        NbtCompound nbtCompound = gun.getOrCreateNbt();

        if (!nbtCompound.contains("Items")) return Optional.empty();

        NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);

        if (nbtList.isEmpty()) return Optional.empty();

        NbtCompound nbtCompound2 = nbtList.getCompound(0);
        ItemStack attachment = ItemStack.fromNbt(nbtCompound2);
        nbtList.remove(0);

        AttachmentItem.AttachType attachType = ((AttachmentItem) attachment.getItem()).getAttachType();

        switch(attachType)
        {
            case SIGHT -> nbtCompound.putInt("sightID", 0);
            case GRIP -> nbtCompound.putInt("gripID", 0);
            case MUZZLE -> nbtCompound.putInt("muzzleID", 0);
        }

        if (nbtList.isEmpty()) gun.removeSubNbt("Items");

        return Optional.of(attachment);
    }

    private int getExistingAttachments(ItemStack gun)
    {
        return getAttachments(gun).mapToInt(ItemStack::getCount).sum();
    }

    private Stream<ItemStack> getAttachments(ItemStack gun)
    {
        NbtCompound nbtCompound = gun.getNbt();
        if (nbtCompound == null)
        {
            return Stream.empty();
        }
        NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
        return nbtList.stream().map(NbtCompound.class::cast).map(ItemStack::fromNbt);
    }

    private Optional<NbtCompound> checkExistingAttachType(ItemStack attachment, NbtList items)
    {
        if (attachment.isOf(Items.BUNDLE)) {
            return Optional.empty();
        }

        //items.stream().filter(NbtCompound.class::isInstance).map(NbtCompound.class::cast).filter(item -> ItemStack.canCombine(ItemStack.fromNbt(item), stack)).findFirst();
        return items.stream().filter(NbtCompound.class::isInstance).map(NbtCompound.class::cast).filter(item ->
                (ItemStack.fromNbt(item).getItem() instanceof AttachmentItem)
                && (((AttachmentItem)ItemStack.fromNbt(item).getItem()).getAttachType() == ((AttachmentItem)attachment.getItem()).getAttachType()))
                .findFirst();
    }

    private boolean acceptedAttachment(AttachmentItem.AttachType attachType)
    {
        for (AttachmentItem.AttachType attachType2 : acceptedAttachmentTypes)
        {
            if (attachType == attachType2) return true;
        }
        return false;
    }

    @Override
    public Predicate<ItemStack> getProjectiles()
    {
        if (this.ammoType == null) {
            return (itemStack) -> false;
        }
        return (itemStack) -> itemStack.isOf(this.ammoType);
    }

    @Override
    public int getRange()
    {
        return 0;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return animationCache;
    }

    public FiringType getFiringType()
    {
        return this.firingType;
    }

    public boolean isScoped()
    {
        return this.isScoped;
    }

    public ArmType getArmType()
    {
        return this.armType;
    }

    public int getSightID(ItemStack gun)
    {
        NbtCompound gunNBT = gun.getOrCreateNbt();
        if (!gunNBT.contains("sightID")) gunNBT.putInt("sightID", 0);

        return gun.getOrCreateNbt().getInt("sightID");
    }

    public int getGripID(ItemStack gun)
    {
        NbtCompound gunNBT = gun.getOrCreateNbt();
        if (!gunNBT.contains("gripID")) gunNBT.putInt("gripID", 0);

        return gun.getOrCreateNbt().getInt("gripID");
    }

    public int getMuzzleID(ItemStack gun)
    {
        NbtCompound gunNBT = gun.getOrCreateNbt();
        if (!gunNBT.contains("muzzleID")) gunNBT.putInt("muzzleID", 0);

        return gun.getOrCreateNbt().getInt("muzzleID");
    }

    public enum LoadingType
    {
        MAGAZINE,
        PER_CARTRIDGE
    }

    public enum FiringType
    {
        SEMI_AUTO,
        AUTO
    }

    public enum ArmType
    {
        HANDGUN_ONEHAND,
        HANDGUN_TWOHAND,
        REVOLVER_FANNING,
        LONG_GUNS,
        MINIGUN
    }
}
