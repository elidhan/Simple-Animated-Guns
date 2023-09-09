package net.elidhan.anim_guns.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.item.BlueprintItem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BlueprintScreen extends HandledScreen<BlueprintScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier(AnimatedGuns.MOD_ID, "textures/gui/inventory/blueprint.png");
    private int currentBlueprintIndex;

    public BlueprintScreen(BlueprintScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);

        this.backgroundWidth = 102;
        this.backgroundHeight = 96;
    }

    protected void setBlueprint(int index)
    {
        if(index == -1)
        {
            index = BlueprintItem.BLUEPRINT_ITEM_LIST.size()-1;
        }
        else if(index >= BlueprintItem.BLUEPRINT_ITEM_LIST.size())
        {
            index = 0;
        }

        currentBlueprintIndex = index;
    }

    @Override
    protected void init()
    {
        super.init();
        //addDrawableChild(ButtonWidget.builder(Text.literal("Close"), (button) -> this.close()).position(((this.width-this.backgroundWidth)/2)+30, ((this.height-this.backgroundHeight)/2)+2).size(10, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("❮"), (button) -> setBlueprint(currentBlueprintIndex - 1)).position(((this.width-this.backgroundWidth)/2)+30, ((this.height-this.backgroundHeight)/2)+24).size(10, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("❯"), (button) -> setBlueprint(currentBlueprintIndex + 1)).position(((this.width-this.backgroundWidth)/2)+62, ((this.height-this.backgroundHeight)/2)+24).size(10, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Set"), (button) ->
        {
            if (client != null)
            {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeInt(getCurrentBlueprint());
                ClientPlayNetworking.send(AnimatedGuns.SELECT_BLUEPRINT_PACKET_ID, buf);

                this.close();
            }
        }).position(((this.width-this.backgroundWidth)/2)+35, ((this.height-this.backgroundHeight)/2)+48).size(32, 20).build());

        setBlueprint(0);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY)
    {

    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;

        context.drawTexture(TEXTURE, i, j, 0, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
    }

    private int getCurrentBlueprint()
    {
        return this.currentBlueprintIndex;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        context.drawItem(new ItemStack(BlueprintItem.BLUEPRINT_ITEM_LIST.get(getCurrentBlueprint())), 43+(this.width - this.backgroundWidth)/2, 26+(this.height - this.backgroundHeight)/2);
    }
}
