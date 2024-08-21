package mitemod.mcpatcher.api;

import net.minecraft.ResourceLocation;
import org.spongepowered.asm.mixin.Unique;

public interface IFontRenderer {
    float[] charWidthf = new float[0];
    boolean isHD = false;
    float fontAdj = 0;
    default ResourceLocation getDefaultFont() {
        return null;
    }

    default void setDefaultFont(ResourceLocation var1) {
        return;
    }

    default ResourceLocation getHDFont() {
        return null;
    }

    default void setHDFont(ResourceLocation var1) {
        return;
    }
}
