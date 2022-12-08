package net.elidhan.anim_guns.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.item.GunItem;
import net.elidhan.anim_guns.mixininterface.InGameHudMixinInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin implements InGameHudMixinInterface
{
    @Mutable
    @Final
    @Shadow
    private final MinecraftClient client;
    private int scaledWidth;
    private int scaledHeight;
    private static final Identifier GUN_SCOPE = new Identifier(AnimatedGuns.MOD_ID, "textures/misc/gun_scope.png");
    private float gunScopeScale;

    public InGameHudMixin(MinecraftClient client)
    {
        this.client = client;
    }
    @Override
    public void renderGunScopeOverlay(float scale) {
        float f;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUN_SCOPE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        float g = f = (float)Math.min(this.scaledWidth, this.scaledHeight);
        float h = Math.min((float)this.scaledWidth / f, (float)this.scaledHeight / g) * scale;
        float i = f * h;
        float j = g * h;
        float k = ((float)this.scaledWidth - i) / 2.0f;
        float l = ((float)this.scaledHeight - j) / 2.0f;
        float m = k + i;
        float n = l + j;
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(k, n, -90.0).texture(0.0f, 1.0f).next();
        bufferBuilder.vertex(m, n, -90.0).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(m, l, -90.0).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(k, l, -90.0).texture(0.0f, 0.0f).next();
        tessellator.draw();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(0.0, this.scaledHeight, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.scaledWidth, this.scaledHeight, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.scaledWidth, n, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(0.0, n, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(0.0, l, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.scaledWidth, l, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.scaledWidth, 0.0, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(0.0, 0.0, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(0.0, n, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(k, n, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(k, l, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(0.0, l, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(m, n, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.scaledWidth, n, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.scaledWidth, l, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(m, l, -90.0).color(0, 0, 0, 255).next();
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void render(MatrixStack matrices, float tickDelta, CallbackInfo ci)
    {
        this.scaledWidth = this.client.getWindow().getScaledWidth();
        this.scaledHeight = this.client.getWindow().getScaledHeight();

        float f = this.client.getLastFrameDuration();
        this.gunScopeScale = MathHelper.lerp(0.5f * f, this.gunScopeScale, 1.125f);
        boolean holdingScopedGun = this.client.player != null
                && this.client.player.getMainHandStack().getItem() instanceof GunItem
                && this.client.player.getMainHandStack().getOrCreateNbt().getInt("Clip") > 0
                && this.client.player.getMainHandStack().getOrCreateNbt().getBoolean("isScoped");

        if (this.client.options.getPerspective().isFirstPerson())
        {
            if (this.client.player.isSneaking() && holdingScopedGun)
            {
                this.renderGunScopeOverlay(this.gunScopeScale);
            }
            else
            {
                this.gunScopeScale = 0.5f;
            }
        }
    }
}
