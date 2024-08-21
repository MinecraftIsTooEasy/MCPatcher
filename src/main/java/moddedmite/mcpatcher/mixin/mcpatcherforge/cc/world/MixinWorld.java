package moddedmite.mcpatcher.mixin.mcpatcherforge.cc.world;

import net.minecraft.Entity;
import net.minecraft.Vec3;
import net.minecraft.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.prupe.mcpatcher.cc.ColorizeWorld;
import com.prupe.mcpatcher.cc.Colorizer;

@Mixin(World.class)
public abstract class MixinWorld {

    @Inject(
        method = "getSkyColor",
        at = @At("HEAD"),
        remap = false)
    private void modifyGetSkyColorBody1(Entity entity, float p_72833_2_, CallbackInfoReturnable<Vec3> cir,
        @Share("computeSkyColor") LocalBooleanRef computeSkyColor) {
        computeSkyColor.set(ColorizeWorld.computeSkyColor((World) (Object) this, p_72833_2_));
    }

    @Inject(
        method = "getSkyColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/BiomeGenBase;getSkyColorByTemp(F)I",
            shift = At.Shift.AFTER,
            remap = false),
        remap = false)
    private void modifyGetSkyColorBody2(Entity entity, float p_72833_2_, CallbackInfoReturnable<Vec3> cir) {
        ColorizeWorld.setupForFog(entity);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(
        method = "getSkyColor",
        at = @At(value = "STORE", ordinal = 0),
        ordinal = 3,
        remap = false)
    private float modifyGetSkyColorBody3(float input, @Share("computeSkyColor") LocalBooleanRef computeSkyColor) {
        if (computeSkyColor.get()) {
            return Colorizer.setColor[0];
        }
        return input;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(
        method = "getSkyColor",
        at = @At(value = "STORE", ordinal = 0),
        ordinal = 4,
        remap = false)
    private float modifyGetSkyColorBody4(float input, @Share("computeSkyColor") LocalBooleanRef computeSkyColor) {
        if (computeSkyColor.get()) {
            return Colorizer.setColor[1];
        }
        return input;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(
        method = "getSkyColor",
        at = @At(value = "STORE", ordinal = 0),
        ordinal = 5,
        remap = false)
    private float modifyGetSkyColorBody5(float input, @Share("computeSkyColor") LocalBooleanRef computeSkyColor) {
        if (computeSkyColor.get()) {
            return Colorizer.setColor[2];
        }
        return input;
    }
}
