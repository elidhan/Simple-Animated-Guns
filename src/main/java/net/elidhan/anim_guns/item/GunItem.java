package net.elidhan.anim_guns.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.AnimatedGunsClient;
import net.elidhan.anim_guns.util.InventoryUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.elidhan.anim_guns.util.RaycastUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.Random;

public abstract class GunItem
extends Item
implements FabricItem, IAnimatable, ISyncable
{
    public final Random random;
    public AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public static final String controllerName = "controller";
    private final String gunID;
    private final String animationID;
    private final float gunDamage;
    private final int rateOfFire;
    private final int magSize;
    public final Item ammoType;
    private final int reloadCooldown;
    private final float bulletSpread;
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
    private final int reloadStage1;
    private final int reloadStage2;
    private final int reloadStage3;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public GunItem(Settings settings, String gunID, String animationID,
                   float gunDamage, int rateOfFire, int magSize,
                   Item ammoType, int reloadCooldown, float bulletSpread,
                   float[] gunRecoil, int pelletCount, LoadingType loadingType,
                   SoundEvent reloadSoundStart, SoundEvent reloadSoundMagOut, SoundEvent reloadSoundMagIn, SoundEvent reloadSoundEnd,
                   SoundEvent shootSound, int reloadCycles, boolean isScoped,
                   int reloadStage1, int reloadStage2, int reloadStage3)
    {
        super(settings.maxDamage((magSize*10)+1));
        GeckoLibNetwork.registerSyncable(this);

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
            user.setCurrentHand(hand);
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
        if(!((PlayerEntity)user).getItemCooldownManager().isCoolingDown(this) && !user.isSprinting() && isLoaded(stack))
        {
            this.shoot(world, (PlayerEntity) user, stack);
            ((PlayerEntity)user).getItemCooldownManager().set(this, this.rateOfFire);
        }
    }

    public void shoot(World world, PlayerEntity user, ItemStack itemStack)
    {
        itemStack.getOrCreateNbt().putInt("reloadTick",0);
        itemStack.getOrCreateNbt().putBoolean("isReloading", false);

        double h_kick = getRecoilX(itemStack);
        float v_kick = getRecoilY(itemStack);

        if (!world.isClient())
        {
            for(int i = 0; i < this.pelletCount; i++)
            {
                int maxDistance = 200;

                Vec3d camUpDirection = RaycastUtil.rotVec(user.getPitch()-90, user.getYaw());

                /*
                Vec3d bulletDirection = user.getRotationVector().add(new Vec3d(
                        this.random.nextGaussian()/64,
                        this.random.nextGaussian()/64,
                        this.random.nextGaussian()/64
                ).multiply(this.bulletSpread));
                */

                Vec3d bulletDirection = user.getRotationVector().add(RaycastUtil.horiSpread(user, random.nextFloat(-bulletSpread*5, bulletSpread*5)));

                HitResult result = getHitResult(world, user, user.getEyePos(), bulletDirection, maxDistance);

                if (result instanceof EntityHitResult entityHitResult) {
                    if (entityHitResult.getEntity().isInvulnerableTo(DamageSource.thrownProjectile(user, user))
                        || entityHitResult.getEntity().isInvulnerable())
                        continue;
                    entityHitResult.getEntity().damage(DamageSource.thrownProjectile(user, user), this.gunDamage);
                    entityHitResult.getEntity().timeUntilRegen = 0;
                } else {
                    BlockHitResult blockHitResult = (BlockHitResult) result;
                    //((ServerWorld) world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, world.getBlockState(blockHitResult.getBlockPos())), blockHitResult.getPos().x, blockHitResult.getPos().y, blockHitResult.getPos().z, 1, 0, 0, 0, 1);
                    ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME, blockHitResult.getPos().x, blockHitResult.getPos().y, blockHitResult.getPos().z, 1, 0, 0, 0, 0);
                }
            }

            //set animation
            final int id = GeckoLibUtil.guaranteeIDForStack(itemStack, (ServerWorld) world);
            GeckoLibNetwork.syncAnimation(user, this, id, !itemStack.getOrCreateNbt().getBoolean("isScoped") && itemStack.getOrCreateNbt().getBoolean("isAiming")?7:1);
            for (PlayerEntity otherPlayer : PlayerLookup.tracking(user))
            {
                GeckoLibNetwork.syncAnimation(otherPlayer, this, id, !itemStack.getOrCreateNbt().getBoolean("isScoped") && itemStack.getOrCreateNbt().getBoolean("isAiming")?7:1);
            }

            //recoil
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeFloat(v_kick);
            buf.writeDouble(h_kick);
            ServerPlayNetworking.send(((ServerPlayerEntity) user), AnimatedGuns.RECOIL_PACKET_ID, buf);
        }

        if(!user.getAbilities().creativeMode)
        {
            itemStack.getOrCreateNbt().putInt("Clip", itemStack.getOrCreateNbt().getInt("Clip") - 1);
            itemStack.damage(10, user, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }

        world.playSound(null,
                user.getX(),
                user.getY(),
                user.getZ(),
                shootSound,SoundCategory.MASTER,1.0f, 1.0f);

        if(this.reloadCycles > 1)
        {
            itemStack.getOrCreateNbt().putInt("currentCycle", itemStack.getOrCreateNbt().getInt("Clip"));
        }
    }

    public HitResult getHitResult(World world, PlayerEntity player, Vec3d origin, Vec3d direction, double maxDistance)
    {
        Vec3d destination = origin.add(direction.multiply(maxDistance));
        HitResult hitResult = world.raycast(new RaycastContext(origin, destination, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
        if (hitResult.getType() != HitResult.Type.MISS) {
            destination = hitResult.getPos();
        }
        EntityHitResult entityHitResult = ProjectileUtil.getEntityCollision(world, player, origin, destination, player.getBoundingBox().stretch(direction.multiply(maxDistance)).expand(1.0), e -> !e.isSpectator() && e.isAttackable());
        if (entityHitResult != null) {
            hitResult = entityHitResult;
        }
        return hitResult;
    }
    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event)
    {
        if(event.getController().getCurrentAnimation() == null || event.getController().getAnimationState() == AnimationState.Stopped)
        {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("idle",ILoopType.EDefaultLoopTypes.LOOP));
        }

        return PlayState.CONTINUE;
    }
    @SuppressWarnings({"rawtypes","unchecked"})
    @Override
    public void registerControllers(AnimationData animationData)
    {
        AnimationController<GunItem> controller = new AnimationController(this, controllerName, 1, this::predicate);
        controller.registerSoundListener(this::soundListener);
        animationData.addAnimationController(controller);
    }
    @SuppressWarnings("rawtypes")
    @Override
    public void onAnimationSync(int id, int state)
    {
        final AnimationController controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
        switch (state)
        {
            case 0 ->
            {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
            }
            case 1 ->
            {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder()
                        .addAnimation("firing", ILoopType.EDefaultLoopTypes.PLAY_ONCE)
                        .addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
            }
            case 2 ->
            {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("reload_start", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            }
            case 3 ->
            {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("reload_magout", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            }
            case 4 ->
            {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("reload_magin", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            }
            case 5 ->
            {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder()
                        .addAnimation("reload_end", ILoopType.EDefaultLoopTypes.PLAY_ONCE)
                        .addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
            }
            case 6 ->
            {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("aim", ILoopType.EDefaultLoopTypes.LOOP));
            }
            case 7 ->
            {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder()
                        .addAnimation("aim_firing", ILoopType.EDefaultLoopTypes.PLAY_ONCE)
                        .addAnimation("aim", ILoopType.EDefaultLoopTypes.LOOP));
            }
            case 8 ->
            {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder()
                        .addAnimation("aim_reload_start", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            }
            case 9 ->
            {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder()
                        .addAnimation("melee", ILoopType.EDefaultLoopTypes.PLAY_ONCE)
                        .addAnimation("idle"));
            }
            case 10 ->
            {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder()
                        .addAnimation("sprinting", ILoopType.EDefaultLoopTypes.LOOP));
            }
        }
    }
    protected <ENTITY extends IAnimatable> void soundListener(SoundKeyframeEvent<ENTITY> event)
    {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            switch (event.sound)
            {
                case "reload_start" ->
                        player.playSound(this.reloadSoundStart, SoundCategory.MASTER, 1, 1);
                case "reload_magout" ->
                        player.playSound(this.reloadSoundMagOut, SoundCategory.MASTER, 1, 1);
                case "reload_magin" ->
                        player.playSound(this.reloadSoundMagIn, SoundCategory.MASTER, 1, 1);
                case "reload_end" ->
                        player.playSound(this.reloadSoundEnd, SoundCategory.MASTER, 1, 1);
                case "melee" ->
                        player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, 1, 1);
            }
        }
    }
    @Override
    public AnimationFactory getFactory()
    {
        return this.factory;
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
        if(world instanceof ServerWorld)
        {
            final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerWorld)world);
            GeckoLibNetwork.syncAnimation(player, this, id, 0);
            for (PlayerEntity otherPlayer : PlayerLookup.tracking(player))
            {
                GeckoLibNetwork.syncAnimation(otherPlayer, this, id, 0);
            }
        }
        super.onCraft(stack, world, player);
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        if(Screen.hasShiftDown())
        {
            tooltip.add(new TranslatableText("Ammo: "+(stack.getOrCreateNbt().getInt("Clip"))+"/"+this.magSize).formatted(Formatting.WHITE));
            tooltip.add(new TranslatableText("Damage: "+this.gunDamage).formatted(Formatting.GRAY));
            tooltip.add(new TranslatableText("Bullet Spread: "+this.bulletSpread).formatted(Formatting.GRAY));
            tooltip.add(new TranslatableText("Recoil: "+this.gunRecoil[1]).formatted(Formatting.GRAY));
            tooltip.add(new TranslatableText("RPM: "+(int)(((float)20/this.rateOfFire)*60)).formatted(Formatting.GRAY));
            tooltip.add(new TranslatableText("Reload Time: "+(float)this.reloadCooldown/20+"s").formatted(Formatting.GRAY));
            tooltip.add(new TranslatableText("Uses:").formatted(Formatting.GRAY));
            tooltip.add(new TranslatableText(this.ammoType.getTranslationKey()).formatted(Formatting.YELLOW));
        }
        else
        {
            tooltip.add(new TranslatableText("Ammo: "+(stack.getOrCreateNbt().getInt("Clip"))+"/"+this.magSize).formatted(Formatting.WHITE));
            tooltip.add(new TranslatableText("Uses:").formatted(Formatting.GRAY));
            tooltip.add(new TranslatableText(this.ammoType.getTranslationKey()).formatted(Formatting.YELLOW));

            tooltip.add(new TranslatableText("Press Shift to see stats").formatted(Formatting.AQUA));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        boolean isSprinting = entity.isSprinting();
        ItemStack mainHandGun = ((PlayerEntity)entity).getMainHandStack();

        if (world.isClient())
        {
            if (((PlayerEntity) entity).getMainHandStack() == stack
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

            if (mainHandGun == stack && !nbtCompound.getBoolean("isReloading"))
            {
                while(AnimatedGunsClient.aimToggle.wasPressed())
                {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBoolean(!stack.getOrCreateNbt().getBoolean("isAiming"));
                    ClientPlayNetworking.send(AnimatedGuns.GUN_AIM_PACKET_ID, buf);
                }
            }

            if(isSprinting
                    && !mainHandGun.getOrCreateNbt().getBoolean("isAiming")
                    && mainHandGun == stack
                    && GeckoLibUtil.getControllerForStack(this.factory, stack, controllerName).getCurrentAnimation() != null
                    && !GeckoLibUtil.getControllerForStack(this.factory, stack, controllerName).getCurrentAnimation().animationName.equals("sprinting")
                    && !GeckoLibUtil.getControllerForStack(this.factory, stack, controllerName).getCurrentAnimation().animationName.equals("melee"))
            {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeItemStack(stack);
                buf.writeBoolean(true);
                ClientPlayNetworking.send(AnimatedGuns.GUN_SPRINT_PACKET_ID, buf);
            }
            else if((!isSprinting || mainHandGun != stack)
                    && GeckoLibUtil.getControllerForStack(this.factory, stack, controllerName).getCurrentAnimation() != null
                    && GeckoLibUtil.getControllerForStack(this.factory, stack, controllerName).getCurrentAnimation().animationName.equals("sprinting"))
            {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeItemStack(stack);
                buf.writeBoolean(false);
                ClientPlayNetworking.send(AnimatedGuns.GUN_SPRINT_PACKET_ID, buf);
            }
        }

        //The actual reload process/tick
        if (nbtCompound.getBoolean("isReloading") && !isSprinting)
        {
            if((mainHandGun != stack
                    || (reserveAmmoCount((PlayerEntity) entity, this.ammoType) <= 0 && this.reloadCycles <= 1)
                    || (nbtCompound.getInt("reloadTick") >= this.reloadCooldown)
                    || (remainingAmmo(stack) >= this.magSize && this.reloadCycles <= 1)))
                nbtCompound.putBoolean("isReloading", false);

            this.doReloadTick(world, nbtCompound, (PlayerEntity)entity, stack);
        }
        else
        {
            if (nbtCompound.getInt("reloadTick") > this.reloadStage3 && nbtCompound.getInt("reloadTick") <= this.reloadCooldown) finishReload((PlayerEntity) entity, stack);

            nbtCompound.putBoolean("isReloading", false);
            nbtCompound.putInt("reloadTick", 0);
        }
    }
    private void doReloadTick(World world, NbtCompound nbtCompound, PlayerEntity player, ItemStack stack)
    {
        int rTick = nbtCompound.getInt("reloadTick");

        if (world instanceof ServerWorld)
        {
            if(nbtCompound.getInt("reloadTick") == this.reloadStage1)
            {
                final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerWorld)world);
                GeckoLibNetwork.syncAnimation(player, this, id, 3);
                for (PlayerEntity otherPlayer : PlayerLookup.tracking(player))
                {
                    GeckoLibNetwork.syncAnimation(otherPlayer, this, id, 3);
                }
            }
            else if(nbtCompound.getInt("reloadTick") == this.reloadStage2)
            {
                final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerWorld)world);
                GeckoLibNetwork.syncAnimation(player, this, id, 4);
                for (PlayerEntity otherPlayer : PlayerLookup.tracking(player))
                {
                    GeckoLibNetwork.syncAnimation(otherPlayer, this, id, 4);
                }
            }
            else if(nbtCompound.getInt("reloadTick") == this.reloadStage3)
            {
                final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerWorld)world);
                GeckoLibNetwork.syncAnimation(player, this, id, 5);
                for (PlayerEntity otherPlayer : PlayerLookup.tracking(player))
                {
                    GeckoLibNetwork.syncAnimation(otherPlayer, this, id, 5);
                }
            }
        }

        nbtCompound.putInt("reloadTick", nbtCompound.getInt("reloadTick") + 1);

        switch(this.loadingType)
        {
            case MAGAZINE:
                if(rTick >= this.reloadCooldown
                        && reserveAmmoCount(player, this.ammoType) > 0)
                {
                    nbtCompound.putInt("currentCycle", 1);
                    finishReload(player, stack);
                    nbtCompound.putInt("reloadTick", 0);
                }
                break;
            case PER_CARTRIDGE:
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
        }
        else
        {
            nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + reserveAmmoCount(player, this.ammoType));
            InventoryUtil.removeItemFromInventory(player, this.ammoType, reserveAmmoCount(player, this.ammoType));
        }

        stack.setDamage(this.getMaxDamage()-((nbtCompound.getInt("Clip")*10)+1));
    }
    public void toggleAim(ItemStack stack, boolean aim, ServerWorld world, PlayerEntity player)
    {
        stack.getOrCreateNbt().putBoolean("isAiming", aim);
        stack.getOrCreateNbt().putBoolean("isScoped",this.isScoped);

        if (aim) player.setSprinting(false);

        final int id = GeckoLibUtil.guaranteeIDForStack(stack, world);
        GeckoLibNetwork.syncAnimation(player, this, id, stack.getOrCreateNbt().getBoolean("isAiming")?6:0);
        for (PlayerEntity otherPlayer : PlayerLookup.tracking(player))
        {
            GeckoLibNetwork.syncAnimation(otherPlayer, this, id, stack.getOrCreateNbt().getBoolean("isAiming")?6:0);
        }
    }
    public void toggleSprint(ItemStack stack, boolean sprint, ServerWorld world, PlayerEntity player)
    {
        final int id = GeckoLibUtil.guaranteeIDForStack(stack, world);
        GeckoLibNetwork.syncAnimation(player, this, id, sprint?10:0);
        for (PlayerEntity otherPlayer : PlayerLookup.tracking(player))
        {
            GeckoLibNetwork.syncAnimation(otherPlayer, this, id, sprint?10:0);
        }
    }
    public void doMeleeAttack(ItemStack itemStack)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeItemStack(itemStack);
        ClientPlayNetworking.send(AnimatedGuns.GUN_MELEE_PACKET_ID, buf);
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
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }
    @Override
    public int getItemBarColor(ItemStack stack)
    {
        return MathHelper.packRgb(0.0f,1f,1f);
    }
    public int getRateOfFire()
    {
        return this.rateOfFire;
    }
    public String getID()
    {
        return this.gunID;
    }
    public String getAnimationID()
    {
        return this.animationID;
    }
    public enum LoadingType
    {
        MAGAZINE,
        PER_CARTRIDGE
    }
}
