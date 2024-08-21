package moddedmite.mcpatcher.mixin.mcpatcherforge.mob;

import net.minecraft.Block;
import net.minecraft.ModelBase;
import net.minecraft.RenderBlocks;
import net.minecraft.RenderLiving;
import net.minecraft.RenderMooshroom;
import net.minecraft.EntityMooshroom;
import net.minecraft.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.prupe.mcpatcher.mob.MobOverlay;

@Mixin(RenderMooshroom.class)
public abstract class MixinRenderMooshroom extends RenderLiving {

    public MixinRenderMooshroom(ModelBase modelBase, float shadowSize) {
        super(modelBase, shadowSize);
    }

    @Inject(method = "renderMooshroomEquippedItems", at = @At("RETURN"))
    private void modifyRenderEquippedItems1(EntityMooshroom entity, float p_77029_2_, CallbackInfo ci) {
        MobOverlay.finishMooshroom();
    }

    @Redirect(
        method = "renderMooshroomEquippedItems",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderMooshroom;bindTexture(Lnet/minecraft/ResourceLocation;)V"))
    private void modifyRenderEquippedItems2(RenderMooshroom renderLiving, ResourceLocation resourceLocation,
        EntityMooshroom entity, float p_77029_2_) {
        this.bindTexture(MobOverlay.setupMooshroom(entity, resourceLocation));
    }

    @WrapWithCondition(
        method = "renderMooshroomEquippedItems",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderBlocks;renderBlockAsItem(Lnet/minecraft/Block;IF)V"))
    private boolean modifyRenderEquippedItems3(RenderBlocks renderBlock, Block block, int i, float f) {
        return !MobOverlay.renderMooshroomOverlay(0.0);
    }
}
