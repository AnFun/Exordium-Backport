package dev.tr7zw.exordium.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "renderModelLists", at = @At("HEAD"))
    protected void renderGuiItem(BakedModel bakedModel, ItemStack itemStack, int i, int j, PoseStack poseStack, VertexConsumer vertexConsumer, CallbackInfo ci) {
        ExordiumModBase.setBlendBypass(true);
    }
    
    @Inject(method = "renderModelLists", at = @At("RETURN"))
    protected void renderGuiItemReturn(BakedModel bakedModel, ItemStack itemStack, int i, int j, PoseStack poseStack, VertexConsumer vertexConsumer, CallbackInfo ci) {
        ExordiumModBase.setBlendBypass(false);
    }
    
    //Workaround for REI, because REI renders Gui items not as Gui Items...
    
    @Inject(method = "render", at = @At("HEAD"))
    public void render(ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean bl, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo ci) {
        if(ExordiumModBase.isForceBlend() && (!ExordiumModBase.isBlendBypass() || ExordiumModBase.getBypassTurnoff() > 0)) {
            ExordiumModBase.setBlendBypass(true);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            ExordiumModBase.setBypassTurnoff(4);
        }
    }
    
//    @Inject(method = "render", at = @At("RETURN"))
//    public void renderReturn(ItemStack itemStack, ItemTransforms.TransformType transformType, boolean bl, PoseStack poseStack,
//            MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo ci) {
//        ExordiumModBase.setBlendBypass(false);
//    }
    
}
