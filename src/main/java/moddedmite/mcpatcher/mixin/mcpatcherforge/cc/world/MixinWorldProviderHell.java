package moddedmite.mcpatcher.mixin.mcpatcherforge.cc.world;

import net.minecraft.EntityLivingBase;
import net.minecraft.Vec3;
import net.minecraft.WorldProviderHell;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.prupe.mcpatcher.cc.ColorizeWorld;

@Mixin(WorldProviderHell.class)
public abstract class MixinWorldProviderHell {

    /**
     * @author Mist475 (adapted from Paul Rupe)
     * @reason customized value
     */
    @Overwrite
    public Vec3 getFogColor(float par1, float par2, EntityLivingBase viewer) {
        return Vec3.createVectorHelper(
            ColorizeWorld.netherFogColor[0],
            ColorizeWorld.netherFogColor[1],
            ColorizeWorld.netherFogColor[2]);
    }
}
