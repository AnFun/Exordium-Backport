package dev.tr7zw.exordium.util;

import java.lang.ref.Cleaner;
import java.util.List;

import com.mojang.blaze3d.vertex.VertexSorting;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.Model.Vector2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.phys.Vec3;

public class SignBufferRenderer {

    private static final Cleaner cleaner = Cleaner.create();
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static Model model = null;
    private RenderTarget guiTarget;
    
    public SignBufferRenderer(SignBlockEntity arg, int light) {
        guiTarget = new TextureTarget((int)ExordiumModBase.signSettings.bufferWidth, (int)ExordiumModBase.signSettings.bufferHeight, false, false);
        guiTarget.setClearColor(0, 0, 0, 0);
        guiTarget.clear(false);
        cleaner.register(this, new State(guiTarget));
        if(model == null)
            initializeModel();
        // restore renderlogic
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }
    
    public void refreshImage(SignBlockEntity arg, int light) {
        ExordiumModBase.instance.getDelayedRenderCallManager().addRenderCall(() -> {
            guiTarget.bindWrite(false);
            guiTarget.clear(false);
        });
    }
    
    private static void initializeModel(){
        float height = (int)ExordiumModBase.signSettings.renderHeight;
        float width = (int)ExordiumModBase.signSettings.renderWidth;

        Vector3f[] modelData = new Vector3f[]{
            new Vector3f(0.0f, height, 0.01F),
            new Vector3f(width, height, 0.01F),
            new Vector3f(width, 0.0f, 0.01F),
            new Vector3f(0.0f, 0.0f, 0.01F),
        };
        Vector2f[] uvData = new Vector2f[]{
            new Vector2f(0.0f, 0.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(1.0f, 1.0f),
            new Vector2f(0.0f, 1.0f),
        };
        model = new Model(modelData, uvData);
    }

    Vec3 getTextOffset(float f) {
        return new Vec3(0.0D, (0.5F * f), (0.07F * f));
    }
    
    public void render(PoseStack poseStack, int light) {
        poseStack.pushPose();
        float g = 0.015625F * 0.6666667F;
        Vec3 vec3 = getTextOffset(0.6666667F);
        poseStack.translate(vec3.x, vec3.y, vec3.z);
        poseStack.scale(g, -g, g);
        poseStack.translate(ExordiumModBase.signSettings.offsetX , ExordiumModBase.signSettings.offsetY, 0);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, guiTarget.getColorTextureId());
        Matrix4f pose = poseStack.last().pose();
        
        model.draw(pose); // TODO: is light required here, since it's baked into the texture?

        poseStack.popPose();
    }

    private static int getDarkColor(SignBlockEntity signBlockEntity) {
        int i = signBlockEntity.getFrontText().getColor().getTextColor();
        if (i == DyeColor.BLACK.getTextColor() && signBlockEntity.getFrontText().hasGlowingText())
            return -988212;
        int j = (int) (FastColor.ARGB32.red(i) * 0.4D);
        int k = (int) (FastColor.ARGB32.green(i) * 0.4D);
        int l = (int) (FastColor.ARGB32.blue(i) * 0.4D);
        return FastColor.ARGB32.color(0, j, k, l);
    }

    static class State implements Runnable {

        private RenderTarget cleanableRenderTarget;
        
        State(RenderTarget guiTarget) {
            this.cleanableRenderTarget = guiTarget;
        }

        public void run() {
            RenderSystem.recordRenderCall(() -> {
                cleanableRenderTarget.destroyBuffers();
            });
        }
    }
    
}
