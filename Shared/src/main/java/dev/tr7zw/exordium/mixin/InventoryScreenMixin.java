package dev.tr7zw.exordium.mixin;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;

/**
 * FIXME: this is just applying to the inventory player. Find a more general way that works for 
 *
 */
@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    @Inject(method = "renderEntityInInventoryFollowsMouse", at = @At("HEAD"))
    private static void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, float f, float g, float h, LivingEntity livingEntity, CallbackInfo ci) {
        ExordiumModBase.setBlendBypass(true);
    }
    
    @Inject(method = "renderEntityInInventoryFollowsMouse", at = @At("RETURN"))
    private static void renderReturn(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, float f, float g, float h, LivingEntity livingEntity, CallbackInfo ci) {
        ExordiumModBase.setBlendBypass(false);
    }
    
}
