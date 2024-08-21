package moddedmite.mcpatcher.mixin.mcpatcherforge.cc.client.particle;

import net.minecraft.EntityFX;
import net.minecraft.EntitySuspendFX;
import net.minecraft.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.prupe.mcpatcher.cc.ColorizeEntity;
import com.prupe.mcpatcher.cc.Colorizer;

@Mixin(EntitySuspendFX.class)
public abstract class MixinEntitySuspendFX extends EntityFX {

    protected MixinEntitySuspendFX(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Inject(method = "<init>(Lnet/minecraft/World;DDDDDD)V", at = @At("RETURN"))
    private void modifyConstructor(World world, double x, double y, double z, double motionX, double motionY,
        double motionZ, CallbackInfo ci) {
        ColorizeEntity.computeSuspendColor(6710962, (int) x, (int) y, (int) z);
        this.particleRed = Colorizer.setColor[0];
        this.particleGreen = Colorizer.setColor[1];
        this.particleBlue = Colorizer.setColor[2];
    }
}
