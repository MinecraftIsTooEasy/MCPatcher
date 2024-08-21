package moddedmite.mcpatcher.mixin.mcpatcherforge.cc.world;

import net.minecraft.EntityLivingBase;
import net.minecraft.Vec3;
import net.minecraft.WorldProviderEnd;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.prupe.mcpatcher.cc.ColorizeWorld;

@Mixin(WorldProviderEnd.class)
public abstract class MixinWorldProviderEnd {

    /**
     * @author Mist475 (adapted from Paul Rupe)
     * @reason customized value
     */
    @Overwrite
    public Vec3 getFogColor(float par1, float par2, EntityLivingBase viewer) {
        return Vec3.createVectorHelper(
            ColorizeWorld.endFogColor[0],
            ColorizeWorld.endFogColor[1],
            ColorizeWorld.endFogColor[2]);
    }
}
