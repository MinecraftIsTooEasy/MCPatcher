package moddedmite.mcpatcher.mixin.mcpatcherforge.sky;

import java.util.ArrayList;
import java.util.List;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.EffectRenderer;
import net.minecraft.EntityFX;
import net.minecraft.TextureManager;
import net.minecraft.Entity;
import net.minecraft.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.prupe.mcpatcher.sky.FireworksHelper;

@SuppressWarnings({ "rawtypes" })
@Mixin(EffectRenderer.class)
public abstract class MixinEffectRenderer {

    @Shadow
    private List[] fxLayers;

    @Inject(
        method = "<init>(Lnet/minecraft/World;Lnet/minecraft/TextureManager;)V",
        at = @At("RETURN"))
    private void modifyConstructor(World world, TextureManager manager, CallbackInfo ci) {
        this.fxLayers = new List[5];
        for (int i = 0; i < this.fxLayers.length; ++i) {
            this.fxLayers[i] = new ArrayList();
        }
    }

    @Redirect(
        method = "addEffect(Lnet/minecraft/EntityFX;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/EntityFX;getFXLayer()I"))
    private int modifyAddEffect(EntityFX instance) {
        return FireworksHelper.getFXLayer(instance);
    }

    @ModifyConstant(
        method = { "updateEffects()V", "clearEffects(Lnet/minecraft/World;)V" },
        constant = @Constant(intValue = 4))
    private int modifyListSize(int constant) {
        return 5;
    }

    @ModifyConstant(method = "renderParticles(Lnet/minecraft/Entity;F)V", constant = @Constant(intValue = 3))
    private int modifyRenderParticles1(int constant) {
        return 5;
    }

    //TODO
//    @Inject(
//            method = "renderParticles(Lnet/minecraft/Entity;F)V",
//            at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z")
//    )
//    private void modifyRenderParticles2(Entity par1Entity, float par2, CallbackInfo ci, @Local int i, @Local(ordinal = 0) @Share("renderParticlesIndex") LocalIntRef renderParticlesIndex) {
//        renderParticlesIndex.set(i);
//    }

    @Redirect(
        method = "renderParticles(Lnet/minecraft/Entity;F)V",
        at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean modifyRenderParticles3(List layer,
        @Share("renderParticlesIndex") LocalIntRef renderParticlesIndex) {
        return FireworksHelper
            .skipThisLayer(this.fxLayers[renderParticlesIndex.get()].isEmpty(), renderParticlesIndex.get());
    }

    @Redirect(
        method = "renderParticles(Lnet/minecraft/Entity;F)V",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBlendFunc(II)V", remap = false))
    private void modifyRenderParticles4(int sfactor, int dfactor,
        @Share("renderParticlesIndex") LocalIntRef renderParticlesIndex) {
        FireworksHelper.setParticleBlendMethod(renderParticlesIndex.get(), 0, true);
    }
}
