package net.elidhan.anim_guns;

import mod.azure.azurelib.animatable.GeoItem;
import net.elidhan.anim_guns.entity.projectile.BulletProjectileEntity;
import net.elidhan.anim_guns.item.BlueprintBundleItem;
import net.elidhan.anim_guns.item.BlueprintItem;
import net.elidhan.anim_guns.item.GunItem;
import net.elidhan.anim_guns.item.ModItems;
import net.elidhan.anim_guns.screen.BlueprintScreenHandler;
import net.elidhan.anim_guns.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;

import static net.elidhan.anim_guns.item.ModItems.HARDENED_IRON_INGOT;
import static net.elidhan.anim_guns.item.ModItems.HARDENED_IRON_NUGGET;

public class AnimatedGuns implements ModInitializer {
    public static final String MOD_ID = "anim_guns";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Identifier RECOIL_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "recoil");
    public static final Identifier RELOAD_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "reload");
    public static final Identifier SELECT_BLUEPRINT_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "select_blueprint");
    public static final Identifier GUN_MELEE_PACKET_CLIENT_ID = new Identifier(AnimatedGuns.MOD_ID, "gun_melee_client");
    public static final Identifier GUN_MELEE_PACKET_SERVER_ID = new Identifier(AnimatedGuns.MOD_ID, "gun_melee_server");
    public static final Identifier GUN_AIM_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "aim");
    public static final Identifier GUN_SPRINT_PACKET_ID = new Identifier(AnimatedGuns.MOD_ID, "sprint");

    public static final ItemGroup MISC = FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.MAGNUM_REVOLVER_BLUEPRINT)).entries((displayContext, entries) -> {
        entries.add(new ItemStack(ModItems.HARDENED_IRON_INGOT));
        entries.add(new ItemStack(ModItems.HARDENED_IRON_NUGGET));
        entries.add(new ItemStack(ModItems.PLASTIC));
        entries.add(new ItemStack(ModItems.ENRICHED_IRON));
        entries.add(new ItemStack(ModItems.PISTOL_GRIP));
        entries.add(new ItemStack(ModItems.GUN_SCOPE));
        entries.add(new ItemStack(ModItems.LONG_BARREL));
        entries.add(new ItemStack(ModItems.SHORT_BARREL));
        entries.add(new ItemStack(ModItems.WOODEN_STOCK));
        entries.add(new ItemStack(ModItems.MODERN_STOCK));
        entries.add(new ItemStack(ModItems.WOODEN_HANDGUARD));
        entries.add(new ItemStack(ModItems.MODERN_HANDGUARD));
        entries.add(new ItemStack(ModItems.PISTOL_MAGAZINE));
        entries.add(new ItemStack(ModItems.REVOLVER_CHAMBER));
        entries.add(new ItemStack(ModItems.SMG_MAGAZINE));
        entries.add(new ItemStack(ModItems.RIFLE_MAGAZINE));
        entries.add(new ItemStack(ModItems.TUBE_MAGAZINE));
        entries.add(new ItemStack(ModItems.LMG_AMMO_BOX));
        entries.add(new ItemStack(ModItems.BLUEPRINT_BUNDLE));
        entries.add(new ItemStack(ModItems.PISTOL_BLUEPRINT));
        entries.add(new ItemStack(ModItems.HEAVY_PISTOL_BLUEPRINT));
        entries.add(new ItemStack(ModItems.MAGNUM_REVOLVER_BLUEPRINT));
        entries.add(new ItemStack(ModItems.OLD_ARMY_REVOLVER_BLUEPRINT));
        entries.add(new ItemStack(ModItems.MACHINE_PISTOL_BLUEPRINT));
        entries.add(new ItemStack(ModItems.HEAVY_SMG_BLUEPRINT));
        entries.add(new ItemStack(ModItems.LIGHT_ASSAULT_RIFLE_BLUEPRINT));
        entries.add(new ItemStack(ModItems.HEAVY_ASSAULT_RIFLE_BLUEPRINT));
        entries.add(new ItemStack(ModItems.WAR_TORN_ASSAULT_RIFLE_BLUEPRINT));
        entries.add(new ItemStack(ModItems.COMBAT_SHOTGUN_BLUEPRINT));
        entries.add(new ItemStack(ModItems.RIOT_SHOTGUN_BLUEPRINT));
        entries.add(new ItemStack(ModItems.DOUBLE_BARRELED_SHOTGUN_BLUEPRINT));
        entries.add(new ItemStack(ModItems.CLASSIC_SNIPER_RIFLE_BLUEPRINT));
        entries.add(new ItemStack(ModItems.BRUSH_GUN_BLUEPRINT));
        entries.add(new ItemStack(ModItems.LMG_BLUEPRINT));
        entries.add(new ItemStack(ModItems.STANDARD_HANDGUN_BULLET));
        entries.add(new ItemStack(ModItems.HEAVY_HANDGUN_BULLET));
        entries.add(new ItemStack(ModItems.STANDARD_RIFLE_BULLET));
        entries.add(new ItemStack(ModItems.HEAVY_RIFLE_BULLET));
        entries.add(new ItemStack(ModItems.SHOTGUN_SHELL));
    }).displayName(Text.translatable("misc")).build();
    public static final ItemGroup GUNS = FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.MAGNUM_REVOLVER)).entries((displayContext, entries) -> {
        entries.add(new ItemStack(ModItems.PISTOL));
        entries.add(new ItemStack(ModItems.HEAVY_PISTOL));
        entries.add(new ItemStack(ModItems.MAGNUM_REVOLVER));
        entries.add(new ItemStack(ModItems.OLD_ARMY_REVOLVER));
        entries.add(new ItemStack(ModItems.MACHINE_PISTOL));
        entries.add(new ItemStack(ModItems.HEAVY_SMG));
        entries.add(new ItemStack(ModItems.LIGHT_ASSAULT_RIFLE));
        entries.add(new ItemStack(ModItems.HEAVY_ASSAULT_RIFLE));
        entries.add(new ItemStack(ModItems.WAR_TORN_ASSAULT_RIFLE));
        entries.add(new ItemStack(ModItems.DOUBLE_BARRELED_SHOTGUN));
        entries.add(new ItemStack(ModItems.COMBAT_SHOTGUN));
        entries.add(new ItemStack(ModItems.RIOT_SHOTGUN));
        entries.add(new ItemStack(ModItems.CLASSIC_SNIPER_RIFLE));
        entries.add(new ItemStack(ModItems.BRUSH_GUN));
        entries.add(new ItemStack(ModItems.LMG));
    }).displayName(Text.translatable("guns")).build();

    public static final ScreenHandlerType<BlueprintScreenHandler> BLUEPRINT_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(new Identifier(MOD_ID, "blueprint_screenhandler"), BlueprintScreenHandler::new);

    public static final EntityType<BulletProjectileEntity> BulletEntityType = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AnimatedGuns.MOD_ID, "bullet"),
            FabricEntityTypeBuilder.<BulletProjectileEntity>create(SpawnGroup.MISC, BulletProjectileEntity::new).dimensions(EntityDimensions.fixed(0.0625f, 0.0625f)).trackRangeBlocks(4).trackedUpdateRate(10).build());

    @Override
    public void onInitialize() {
        ModItems.registerModItems();
        ModSounds.registerSounds();

        Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, "anim_guns.misc"), MISC);
        Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, "anim_guns.guns"), GUNS);

        ServerPlayNetworking.registerGlobalReceiver(RELOAD_PACKET_ID, (server, player, serverPlayNetworkHandler, buf, packetSender) ->
        {
            if (player.getMainHandStack().getItem() instanceof GunItem gunItem) {
                ItemStack stack = player.getMainHandStack();
                stack.getOrCreateNbt().putBoolean("isReloading", buf.readBoolean());

                final long id = GeoItem.getOrAssignId(stack, (ServerWorld) player.getWorld());
                gunItem.triggerAnim(player, id, "controller", stack.getOrCreateNbt().getBoolean("isAiming") ? "aim_reload_start" : "reload_start");

                stack.getOrCreateNbt().putBoolean("isAiming", false);
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(SELECT_BLUEPRINT_PACKET_ID, (server, player, serverPlayNetworkHandler, buf, packetSender) ->
        {
            int i = buf.readInt();
            Item blueprint = BlueprintItem.BLUEPRINT_ITEM_LIST.get(i);

            if (player.getMainHandStack().getItem() instanceof BlueprintItem || player.getMainHandStack().getItem() instanceof BlueprintBundleItem) {
                player.getMainHandStack().decrement(1);
            } else if (player.getOffHandStack().getItem() instanceof BlueprintItem || player.getOffHandStack().getItem() instanceof BlueprintBundleItem) {
                player.getOffHandStack().decrement(1);
            }

            if (player.getInventory().getEmptySlot() > -1) {
                player.giveItemStack(new ItemStack(blueprint));
            } else {
                player.dropItem(new ItemStack(blueprint), false, true);
            }

        });

        ServerPlayNetworking.registerGlobalReceiver(GUN_MELEE_PACKET_SERVER_ID, (server, player, serverPlayNetworkHandler, buf, packetSender) ->
        {
            ItemStack stack = buf.readItemStack();

            //((GunItem) stack.getItem()).aimAnimation(player.getMainHandStack(), false, (ServerWorld) player.getWorld(), player);

            float i = player.getItemCooldownManager().getCooldownProgress(stack.getItem(), 0) * ((GunItem) stack.getItem()).getRateOfFire();
            if ((int) i < 4) player.getItemCooldownManager().set(stack.getItem(), 10);

            final long id = GeoItem.getOrAssignId(stack, (ServerWorld) player.getWorld());
            ((GunItem) stack.getItem()).triggerAnim(player, id, "controller", "melee");

            ServerPlayNetworking.send(player, GUN_MELEE_PACKET_CLIENT_ID, PacketByteBufs.empty());
        });
        ServerPlayNetworking.registerGlobalReceiver(GUN_AIM_PACKET_ID, (server, player, serverPlayNetworkHandler, buf, packetSender) ->
        {
            if (player.getMainHandStack().getItem() instanceof GunItem) {
                ((GunItem) player.getMainHandStack().getItem()).aimAnimation(player.getMainHandStack(), buf.readBoolean(), (ServerWorld) player.getWorld(), player);
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(GUN_SPRINT_PACKET_ID, (server, player, serverPlayNetworkHandler, buf, packetSender) ->
        {
            ItemStack stack = buf.readItemStack();

            if (stack.getItem() instanceof GunItem) {
                if (player.getWorld() instanceof ServerWorld serverWorld) {
                    ((GunItem) stack.getItem()).toggleSprint(stack, buf.readBoolean(), serverWorld, player);
                }
            }
        });
    }
}
