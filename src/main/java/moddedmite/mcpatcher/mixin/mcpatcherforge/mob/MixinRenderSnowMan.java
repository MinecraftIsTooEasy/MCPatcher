package moddedmite.mcpatcher.mixin.mcpatcherforge.mob;

import net.minecraft.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.prupe.mcpatcher.mob.MobOverlay;

@Mixin(RenderSnowMan.class)
public abstract class MixinRenderSnowMan {

    @WrapWithCondition(
        method = "renderEquippedItems",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderSnowMan;renderSnowmanPumpkin(Lnet/minecraft/EntitySnowman;F)V"))
    private boolean modifyRenderEquippedItems(RenderSnowMan instance, EntitySnowman entitySnowman, float par1EntitySnowman) {
        return !MobOverlay.renderSnowmanOverlay(entitySnowman);
    }
}
