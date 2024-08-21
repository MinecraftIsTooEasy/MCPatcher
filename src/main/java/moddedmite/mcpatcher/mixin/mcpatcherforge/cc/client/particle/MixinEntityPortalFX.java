package jss.notfine.mixins.early.mcpatcherforge.cc.client.particle;

import net.minecraft.EntityFX;
import net.minecraft.EntityPortalFX;
import net.minecraft.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.prupe.mcpatcher.cc.ColorizeEntity;

@Mixin(EntityPortalFX.class)
public abstract class MixinEntityPortalFX extends EntityFX {

    @Shadow
    private float portalParticleScale;

    protected MixinEntityPortalFX(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void modifyConstructor(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, int type, CallbackInfo ci) {
        // green & red get multiplied in constructor, blue doesn't
        this.particleGreen = this.portalParticleScale / 0.3f;
        this.particleGreen *= ColorizeEntity.portalColor[1];
        this.particleRed = this.portalParticleScale / 0.9f;
        this.particleRed *= ColorizeEntity.portalColor[0];
        this.particleBlue = ColorizeEntity.portalColor[2];
    }
}
