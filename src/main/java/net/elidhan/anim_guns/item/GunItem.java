package net.elidhan.anim_guns.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.SingletonGeoAnimatable;
import mod.azure.azurelib.animatable.client.RenderProvider;
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
import net.elidhan.anim_guns.client.render.GunRenderer;
import net.elidhan.anim_guns.entity.projectile.BulletProjectileEntity;
import net.elidhan.anim_guns.sound.ModSounds;
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
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class GunItem extends Item implements FabricItem, GeoAnimatable, GeoItem
{
    protected static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    public final Random random;
    public static final String controllerName = "controller";
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
    private final int reloadCycles;
    private final boolean isScoped;
    private final boolean unscopeAfterShot;
    private final int reloadStage1;
    private final int reloadStage2;
    private final int reloadStage3;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    protected final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    protected final AnimatableInstanceCache animationCache = AzureLibUtil.createInstanceCache(this);

    public GunItem(Settings settings, String gunID, String animationID,
                   float gunDamage, int rateOfFire, int magSize,
                   Item ammoType, int reloadCooldown, float[] bulletSpread,
                   float[] gunRecoil, int pelletCount, LoadingType loadingType,
                   SoundEvent reloadSoundStart, SoundEvent reloadSoundMagOut, SoundEvent reloadSoundMagIn, SoundEvent reloadSoundEnd,
                   SoundEvent shootSound, int reloadCycles, boolean isScoped, boolean unscopeAfterShot,
                   int reloadStage1, int reloadStage2, int reloadStage3)
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
        this.reloadCycles = reloadCycles;
        this.isScoped = isScoped;
        this.unscopeAfterShot = unscopeAfterShot;
        this.reloadStage1 = reloadStage1;
        this.reloadStage2 = reloadStage2;
        this.reloadStage3 = reloadStage3;
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", 16, EntityAttributeModifier.Operation.ADDITION));
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
                BulletProjectileEntity bullet = new BulletProjectileEntity(user, world, this.gunDamage);

                bullet.setPosition(user.getX(), user.getEyeY(), user.getZ());

                Vec3d vertiSpread = BulletUtil.vertiSpread(user, (random.nextFloat(-bulletSpread[0] * 5, bulletSpread[0] * 5)));
                Vec3d horiSpread = BulletUtil.horiSpread(user, (random.nextFloat(-bulletSpread[1] * 5, bulletSpread[1] * 5)));

                Vec3d result = user.getRotationVector().add(vertiSpread).add(horiSpread);

                bullet.setVelocity(result.getX(), result.getY(), result.getZ(), 20, 0);
                bullet.setBaseVel(bullet.getVelocity());
                bullet.setOwner(user);

                world.spawnEntity(bullet);
            }

            final long id = GeoItem.getOrAssignId(itemStack, (ServerWorld) world);
            AnimationController<GeoAnimatable> animationController = this.animationCache.getManagerForId(id).getAnimationControllers().get("controller");

            if (animationController.isPlayingTriggeredAnimation() && (animationController.getCurrentRawAnimation().equals(GunAnimations.FIRING) || animationController.getCurrentRawAnimation().equals(GunAnimations.AIM_FIRING)))
            {
                animationController.forceAnimationReset();
            } else
            {
                animationController.tryTriggerAnimation(itemStack.getOrCreateNbt().getBoolean("isAiming") ? "aim_firing" : "firing");
            }

            //recoil
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeFloat(v_kick);
            buf.writeDouble(h_kick);
            ServerPlayNetworking.send(((ServerPlayerEntity) user), AnimatedGuns.RECOIL_PACKET_ID, buf);

            //GunUtil.spawnLightSource(user, false);
        }

        if (!user.getAbilities().creativeMode)
        {
            itemStack.getOrCreateNbt().putInt("Clip", itemStack.getOrCreateNbt().getInt("Clip") - 1);
            itemStack.damage(10, user, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
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

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        if (Screen.hasShiftDown())
        {
            tooltip.add(Text.translatable("Ammo: " + (stack.getOrCreateNbt().getInt("Clip")) + "/" + this.magSize).formatted(Formatting.WHITE));
            tooltip.add(Text.translatable("Damage: " + this.gunDamage).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("Recoil: " + this.gunRecoil[1]).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("RPM: " + (int) (((float) 20 / this.rateOfFire) * 60)).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("Reload Time: " + (float) this.reloadCooldown / 20 + "s").formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("Uses:").formatted(Formatting.GRAY));
            tooltip.add(Text.translatable(this.ammoType.getTranslationKey()).formatted(Formatting.YELLOW));
        } else
        {
            tooltip.add(Text.translatable("Ammo: " + (stack.getOrCreateNbt().getInt("Clip")) + "/" + this.magSize).formatted(Formatting.WHITE));
            tooltip.add(Text.translatable("Uses:").formatted(Formatting.GRAY));
            tooltip.add(Text.translatable(this.ammoType.getTranslationKey()).formatted(Formatting.YELLOW));

            tooltip.add(Text.translatable("Press Shift to see stats").formatted(Formatting.AQUA));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        boolean isSprinting = entity.isSprinting();
        ItemStack mainHandGun = ((PlayerEntity) entity).getMainHandStack();

        if (world.isClient())
        {
            if (mainHandGun == stack
                    && AnimatedGunsClient.reloadToggle.isPressed()
                    && remainingAmmo(stack) < this.magSize
                    && reserveAmmoCount(((PlayerEntity) entity), this.ammoType) > 0
                    && !nbtCompound.getBoolean("isReloading")
                    && !isSprinting)
            {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(true);
                ClientPlayNetworking.send(AnimatedGuns.RELOAD_PACKET_ID, buf);
            }

            if (mainHandGun == stack && !isSprinting)
            {
                while (AnimatedGunsClient.meleeKey.wasPressed())
                {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeItemStack(stack);
                    ClientPlayNetworking.send(AnimatedGuns.GUN_MELEE_PACKET_SERVER_ID, buf);
                }
            }
        } else
        {
            final long id = GeoItem.getOrAssignId(stack, (ServerWorld) world);
            AnimationController<GeoAnimatable> animationController = this.animationCache.getManagerForId(id).getAnimationControllers().get("controller");

            boolean bl = animationController.isPlayingTriggeredAnimation() && (animationController.getCurrentRawAnimation().equals(GunAnimations.SPRINTING));

            if (isSprinting
                    && !mainHandGun.getOrCreateNbt().getBoolean("isAiming")
                    && mainHandGun == stack
                    && !mainHandGun.getOrCreateNbt().getBoolean("isReloading")
                    && !bl)
            {
                animationController.tryTriggerAnimation("sprinting");
            } else if ((!isSprinting || mainHandGun != stack) && bl)
            {
                animationController.tryTriggerAnimation("idle");
            }

            //The actual reload process/tick
            if (nbtCompound.getBoolean("isReloading") && !isSprinting)
            {
                if ((mainHandGun != stack
                        || (reserveAmmoCount((PlayerEntity) entity, this.ammoType) <= 0 && this.reloadCycles <= 1)
                        || (nbtCompound.getInt("reloadTick") >= this.reloadCooldown)
                        || (remainingAmmo(stack) >= this.magSize && this.reloadCycles <= 1)))
                    nbtCompound.putBoolean("isReloading", false);

                this.doReloadTick(world, nbtCompound, (PlayerEntity) entity, stack);
            } else
            {
                if (nbtCompound.getInt("reloadTick") > this.reloadStage3 && nbtCompound.getInt("reloadTick") <= this.reloadCooldown)
                    finishReload((PlayerEntity) entity, stack);

                nbtCompound.putBoolean("isReloading", false);
                nbtCompound.putInt("reloadTick", 0);
            }
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
            } else if (nbtCompound.getInt("reloadTick") == this.reloadStage2)
            {
                triggerAnim(player, id, "controller", "reload_magin");
            } else if (nbtCompound.getInt("reloadTick") == this.reloadStage3)
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
        return stack.getOrCreateNbt().getBoolean("isAiming") ?
                (rd ? this.gunRecoil[0] : -this.gunRecoil[0]) / 2 :
                (rd ? this.gunRecoil[0] : -this.gunRecoil[0]);
    }

    public float getRecoilY(ItemStack stack)
    {
        return stack.getOrCreateNbt().getBoolean("isAiming") ? this.gunRecoil[1] / 2 : this.gunRecoil[1];
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
        triggerAnim(player, id, "controller", stack.getOrCreateNbt().getBoolean("isAiming") ? "aim" : "idle");
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

    public void toggleAim(ItemStack itemStack)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(!itemStack.getOrCreateNbt().getBoolean("isAiming"));
        ClientPlayNetworking.send(AnimatedGuns.GUN_AIM_PACKET_ID, buf);
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
            event.getController().setAnimation(GunAnimations.IDLE);
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
                case "reload_start" -> player.playSound(this.reloadSoundStart, SoundCategory.MASTER, 1, 1);
                case "reload_magout" -> player.playSound(this.reloadSoundMagOut, SoundCategory.MASTER, 1, 1);
                case "reload_magin" -> player.playSound(this.reloadSoundMagIn, SoundCategory.MASTER, 1, 1);
                case "reload_end" -> player.playSound(this.reloadSoundEnd, SoundCategory.MASTER, 1, 1);
                case "melee" -> player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, 1, 1);
            }
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return animationCache;
    }

    public enum LoadingType
    {
        MAGAZINE,
        PER_CARTRIDGE
    }
}
