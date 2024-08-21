package moddedmite.mcpatcher.mixin.mcpatcherforge.cc.client.renderer;

import net.minecraft.RenderGlobal;
import net.minecraft.GameSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.prupe.mcpatcher.cc.ColorizeWorld;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal {

    @ModifyArg(
        method = "renderSky(F)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/Tessellator;setColorOpaque_I(I)V"))
    private int modifyRenderSky2(int endSkyColor) {
        return ColorizeWorld.endSkyColor;
    }

    @Redirect(
        method = "renderClouds(F)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/GameSettings;isFancyGraphicsEnabled()Z"))
    private boolean modifyRenderClouds(GameSettings instance) {
        return ColorizeWorld.drawFancyClouds(instance.isFancyGraphicsEnabled());
    }
}
