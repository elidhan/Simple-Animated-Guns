package net.elidhan.anim_guns.client.render;

import net.elidhan.anim_guns.client.model.GunModel;
import net.elidhan.anim_guns.item.GunItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import software.bernie.geckolib3.util.RenderUtils;

public class GunRenderer extends GeoItemRenderer<GunItem>
{
    public GunRenderer()
    {
        super(new GunModel());
    }

    //I pray to Notch this stuff isn't gonna be the cause of a crash later on.
    private ModelTransformation.Mode transformType;
    private RenderLayer renderType;

    @Override
    public void render(ItemStack stack, ModelTransformation.Mode transformType, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay)
    {
        //Will this break something?
        this.transformType = transformType;
        super.render(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void render(GeoModel model, GunItem animatable, float partialTick, RenderLayer type, MatrixStack poseStack, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        //Will this break something?
        this.renderType = type;
        super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void renderRecursively(GeoBone bone, MatrixStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        //Will THIS break something?
        MinecraftClient client = MinecraftClient.getInstance();

        boolean renderArms = false;

        //Bones malone let's gooo
        switch (bone.getName())
        {
            case "leftArm", "rightArm" ->
            {
                bone.setHidden(true);
                renderArms = true;
            }
            case "stock_0" -> bone.setHidden(currentItemStack.getOrCreateNbt().getInt("stockID") != 0);
            case "stock_1" -> bone.setHidden(currentItemStack.getOrCreateNbt().getInt("stockID") != 1);
            case "stock_2" -> bone.setHidden(currentItemStack.getOrCreateNbt().getInt("stockID") != 2);
            case "muzzle" -> bone.setHidden(currentItemStack.getOrCreateNbt().getInt("muzzleID") == 0);
            case "foregrip" -> bone.setHidden(currentItemStack.getOrCreateNbt().getInt("foregripID") == 0);
            case "sight_0" -> bone.setHidden(currentItemStack.getOrCreateNbt().getInt("sightID") != 0);
            case "sight_1" -> bone.setHidden(currentItemStack.getOrCreateNbt().getInt("sightID") != 1);
            case "sight_2" -> bone.setHidden(currentItemStack.getOrCreateNbt().getInt("sightID") != 2);

            //I have no idea what a packedLight is but it makes the muzzleflah fullbright when I set it to a high number
            //so I'm keeping it like this
            case "muzzleflash" -> packedLight = 255;
        }

        //I just want the arms to show, why do we have to suffer just to get opposable thumbs
        if(renderArms && this.transformType.isFirstPerson())
        {
            PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)client.getEntityRenderDispatcher().getRenderer(client.player);
            PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = playerEntityRenderer.getModel();

            poseStack.push();

            RenderUtils.translateMatrixToBone(poseStack, bone);
            RenderUtils.translateToPivotPoint(poseStack,bone);
            RenderUtils.rotateMatrixAroundBone(poseStack, bone);
            RenderUtils.scaleMatrixForBone(poseStack, bone);
            RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

            assert(client.player != null);

            Identifier playerSkin = client.player.getSkinTexture();
            VertexConsumer arm = this.rtb.getBuffer(RenderLayer.getEntitySolid(playerSkin));
            VertexConsumer sleeve = this.rtb.getBuffer(RenderLayer.getEntityTranslucent(playerSkin));

            if(bone.name.equals("leftArm"))
            {
                //poseStack.scale(0.875f,1f,0.875f);
                poseStack.translate(0,-0.30625,0);
                playerEntityModel.leftArm.setPivot(bone.rotationPointX,bone.rotationPointY,bone.rotationPointZ);
                playerEntityModel.leftArm.setAngles(0,0,0);
                playerEntityModel.leftArm.render(poseStack, arm, packedLight, packedOverlay, 1, 1, 1, 1);

                playerEntityModel.leftSleeve.setPivot(bone.rotationPointX,bone.rotationPointY,bone.rotationPointZ);
                playerEntityModel.leftSleeve.setAngles(0,0,0);
                playerEntityModel.leftSleeve.render(poseStack, sleeve, packedLight, packedOverlay, 1, 1, 1, 1);
            }
            else if (bone.name.equals("rightArm"))
            {
                //poseStack.scale(0.875f,1f,0.875f);
                poseStack.translate(0,-0.30625,0);
                playerEntityModel.rightArm.setPivot(bone.rotationPointX,bone.rotationPointY,bone.rotationPointZ);
                playerEntityModel.rightArm.setAngles(0,0,0);
                playerEntityModel.rightArm.render(poseStack, arm, packedLight, packedOverlay, 1, 1, 1, 1);

                playerEntityModel.rightSleeve.setPivot(bone.rotationPointX,bone.rotationPointY,bone.rotationPointZ);
                playerEntityModel.rightSleeve.setAngles(0,0,0);
                playerEntityModel.rightSleeve.render(poseStack, sleeve, packedLight, packedOverlay, 1, 1, 1, 1);
            }

            poseStack.pop();
        }

        super.renderRecursively(bone, poseStack, this.rtb.getBuffer(this.renderType), packedLight, packedOverlay, red, green, blue, alpha);
    }
}
