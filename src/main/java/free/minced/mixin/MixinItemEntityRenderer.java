package free.minced.mixin;

import free.minced.Minced;
import free.minced.modules.impl.render.ItemPhysic;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class MixinItemEntityRenderer {

    @Shadow
    @Final
    private Random random;

    @Shadow
    @Final
    private ItemRenderer itemRenderer;

    // я ебанутый хаха
    private int getRenderedAmount(ItemStack stack) {
        int i = 1;
        if (stack.getCount() > 48) {
            i = 5;
        } else if (stack.getCount() > 32) {
            i = 4;
        } else if (stack.getCount() > 16) {
            i = 3;
        } else if (stack.getCount() > 1) {
            i = 2;
        }

        return i;
    }

    public void render2(ItemEntity pEntity, float pEntityYaw, float pPartialTicks, MatrixStack pMatrixStack, VertexConsumerProvider pBuffer, int pPackedLight) {
        pMatrixStack.push();
        ItemStack itemstack = pEntity.getStack();
        int i = itemstack.isEmpty() ? 187 : Item.getRawId(itemstack.getItem()) + itemstack.getDamage();

        this.random.setSeed((long)i);
        BakedModel bakedmodel = this.itemRenderer.getModel(itemstack, pEntity.getWorld(), (LivingEntity)null, pEntity.getId());
        boolean flag = bakedmodel.hasDepth();
        int j = this.getRenderedAmount(itemstack);
        float f = 0.25F;
        float f1 = MathHelper.sin(((float)pEntity.getItemAge() + pPartialTicks) / 10.0F + pEntity.uniqueOffset) * 0.1F + 0.1F;



        ItemPhysic module = Minced.getInstance().getModuleHandler().get(ItemPhysic.class);

        boolean moduleEnabled = module != null && module.isEnabled();


        float f2 = bakedmodel.getTransformation().getTransformation(ModelTransformationMode.GROUND).scale.y();
        if (!moduleEnabled)  pMatrixStack.translate(0.0F, f1 + 0.25F * f2, 0.0F);
        float f3 = pEntity.getRotation(pPartialTicks);
        if (!moduleEnabled)  pMatrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(f3));
        if (moduleEnabled) pMatrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pEntity.isOnGround() ? 90 : f3 * 300));

        float f4 = bakedmodel.getTransformation().ground.scale.x();
        float f5 = bakedmodel.getTransformation().ground.scale.y();
        float f6 = bakedmodel.getTransformation().ground.scale.z();
        if (!flag) {
            float f7 = -0.0F * (float)(j - 1) * 0.5F * f4;
            float f8 = -0.0F * (float)(j - 1) * 0.5F * f5;
            float f9 = -0.09375F * (float)(j - 1) * 0.5F * f6;
            pMatrixStack.translate(f7, f8, f9);
        }

        for(int k = 0; k < j; ++k) {
            pMatrixStack.push();
            if (k > 0) {
                if (flag) {
                    float f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
           

                    pMatrixStack.translate(f11, f13, f10);
                } else {
                    float f12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    float f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
            

                    pMatrixStack.translate(f12, f14, 0.0F);
                }
            }

            this.itemRenderer.renderItem(itemstack, ModelTransformationMode.GROUND, false, pMatrixStack, pBuffer, pPackedLight, OverlayTexture.DEFAULT_UV, bakedmodel);
            pMatrixStack.pop();
            if (!flag) {
                pMatrixStack.translate(0.0F * f4, 0.0F * f5, 0.09375F * f6);
            }
        }

        pMatrixStack.pop();
    }

    @Inject(method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void render(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        ci.cancel();

        render2(itemEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
