package mitemod.mcpatcher;

import net.minecraft.EntityRenderer;
import net.minecraft.Minecraft;
import net.minecraft.Potion;

public class EntityRendererMCP extends EntityRenderer {
    public EntityRendererMCP(Minecraft par1Minecraft) {
        super(par1Minecraft);
    }

    public float getNightVisionStrength(float var1) {
        return super.mc.thePlayer.isPotionActive(Potion.nightVision) ? this.getNightVisionBrightness(this.mc.thePlayer, var1) : 0.0F;
    }
}
