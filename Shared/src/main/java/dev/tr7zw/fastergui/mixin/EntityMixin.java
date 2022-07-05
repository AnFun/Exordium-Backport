package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.fastergui.access.NametagBufferHolder;
import dev.tr7zw.fastergui.util.NametagBufferRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public class EntityMixin implements NametagBufferHolder {

    private Component lastNametag = null;
    private boolean lastSneaking = false;
    
    private NametagBufferRenderer nametagBuffer = null;
    
    @Override
    public boolean renderBuffered(Component text, PoseStack arg3, MultiBufferSource arg4, int light, boolean sneaking) {
        if(nametagBuffer == null) { // lazy init
            int size = Minecraft.getInstance().font.width(text);
            if(size <= 0) {
                return false;
            }
            nametagBuffer = new NametagBufferRenderer();
            System.out.println("new buffer");
        }
        if(lastNametag == null || (!lastNametag.getString().equals(text.getString())) || this.lastSneaking != sneaking) {
            nametagBuffer.refreshImage(text, arg4, light, !sneaking);
            lastNametag = text;
            this.lastSneaking = sneaking;
            System.out.println("refresh");
        }
        nametagBuffer.render(arg3, light, sneaking);
        return true;
    }

}
