package moddedmite.mcpatcher.mixin.mcpatcherforge.cc.client.particle;

import net.minecraft.EntityAuraFX;
import net.minecraft.EntityFX;
import net.minecraft.World;

import org.spongepowered.asm.mixin.Mixin;

import com.prupe.mcpatcher.cc.ColorizeEntity;
import com.prupe.mcpatcher.cc.Colorizer;

import jss.notfine.util.EntityAuraFXExpansion;

@Mixin(EntityAuraFX.class)
public abstract class MixinEntityAuraFX extends EntityFX implements EntityAuraFXExpansion {

    protected MixinEntityAuraFX(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public EntityAuraFX colorize() {
        if (ColorizeEntity.computeMyceliumParticleColor()) {
            this.particleRed = Colorizer.setColor[0];
            this.particleGreen = Colorizer.setColor[1];
            this.particleBlue = Colorizer.setColor[2];
        }
        return (EntityAuraFX) (Object) this;
    }
}
