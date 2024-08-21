package moddedmite.mcpatcher.mixin.mcpatcherforge.cc.world;

import com.prupe.mcpatcher.cc.ColorizeWorld;
import net.minecraft.EntityLivingBase;
import net.minecraft.Vec3;
import net.minecraft.WorldProviderUnderworld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WorldProviderUnderworld.class)
public class MixinWorldProviderUnderWorld {
    /**
     * @author Xy_Lose
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
