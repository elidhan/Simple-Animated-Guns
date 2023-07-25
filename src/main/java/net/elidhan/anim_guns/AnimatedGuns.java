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

    }).displayName(Text.translatable("misc")).build();
    public static final ItemGroup GUNS = FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.MAGNUM_REVOLVER)).entries((displayContext, entries) -> {

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
