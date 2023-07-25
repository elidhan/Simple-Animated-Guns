package net.elidhan.anim_guns.client.render;

import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;
import mod.azure.azurelib.renderer.GeoRenderer;
import mod.azure.azurelib.util.RenderUtils;
import net.elidhan.anim_guns.client.MuzzleFlashRenderType;
import net.elidhan.anim_guns.client.model.GunModel;
import net.elidhan.anim_guns.item.GunItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.util.Objects;

public class GunRenderer extends GeoItemRenderer<GunItem> implements GeoRenderer<GunItem>
{
    public GunRenderer(Identifier identifier)
    {
        super(new GunModel(identifier));
    }

    private VertexConsumerProvider bufferSource;
    private ModelTransformationMode transformType;

    @Override
    public void render(ItemStack stack, ModelTransformationMode transformType, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
        this.bufferSource = bufferSource;
        //this.renderType = type;
        this.transformType = transformType;
        super.render(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void renderRecursively(MatrixStack poseStack, GunItem animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        //Will THIS break something? doubt
        MinecraftClient client = MinecraftClient.getInstance();

        boolean renderArms = false;

        //Bones malone let's gooo
        switch (bone.getName()) {
            case "leftArm", "rightArm" -> {
                bone.setHidden(true);
                renderArms = true;
            }
        }

        //I just want the arms to show, why do we have to suffer just to get opposable thumbs
        //  && this.transformType == ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND ***don't mind this, just some backup code in case my dumbass forgets
        if (renderArms && this.transformType == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND) {
            PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer) client.getEntityRenderDispatcher().getRenderer(client.player);
            PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = playerEntityRenderer.getModel();

            poseStack.push();

            RenderUtils.translateMatrixToBone(poseStack, bone);
            RenderUtils.translateToPivotPoint(poseStack, bone);
            RenderUtils.rotateMatrixAroundBone(poseStack, bone);
            RenderUtils.scaleMatrixForBone(poseStack, bone);
            RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

            assert (client.player != null);

            Identifier playerSkin = client.player.getSkinTexture();
            VertexConsumer arm = this.bufferSource.getBuffer(RenderLayer.getEntitySolid(playerSkin));
            VertexConsumer sleeve = this.bufferSource.getBuffer(RenderLayer.getEntityTranslucent(playerSkin));

            if (bone.getName().equals("leftArm")) {
                poseStack.scale(0.67f, 1.33f, 0.67f);
                poseStack.translate(-0.25, -0.43625, 0.1625);
                playerEntityModel.leftArm.setPivot(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
                playerEntityModel.leftArm.setAngles(0, 0, 0);
                playerEntityModel.leftArm.render(poseStack, arm, packedLight, packedOverlay, 1, 1, 1, 1);

                playerEntityModel.leftSleeve.setPivot(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
                playerEntityModel.leftSleeve.setAngles(0, 0, 0);
                playerEntityModel.leftSleeve.render(poseStack, sleeve, packedLight, packedOverlay, 1, 1, 1, 1);
            } else if (bone.getName().equals("rightArm")) {
                poseStack.scale(0.67f, 1.33f, 0.67f);
                poseStack.translate(0.25,-0.43625,0.1625);
                playerEntityModel.rightArm.setPivot(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
                playerEntityModel.rightArm.setAngles(0,0,0);
                playerEntityModel.rightArm.render(poseStack, arm, packedLight, packedOverlay, 1, 1, 1, 1);

                playerEntityModel.rightSleeve.setPivot(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
                playerEntityModel.rightSleeve.setAngles(0,0,0);
                playerEntityModel.rightSleeve.render(poseStack, sleeve, packedLight, packedOverlay, 1, 1, 1, 1);
            }
            poseStack.pop();
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, Objects.equals(bone.getName(), "muzzleflash") ? this.bufferSource.getBuffer(MuzzleFlashRenderType.getMuzzleFlash()) : this.bufferSource.getBuffer(renderType), isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
