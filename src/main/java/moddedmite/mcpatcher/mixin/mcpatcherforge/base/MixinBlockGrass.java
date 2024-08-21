package moddedmite.mcpatcher.mixin.mcpatcherforge.base;

import net.minecraft.Block;
import net.minecraft.BlockGrass;
import net.minecraft.Icon;
import net.minecraft.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.prupe.mcpatcher.mal.block.RenderBlocksUtils;

@Mixin(BlockGrass.class)
public class MixinBlockGrass {

    @Shadow
    private Icon iconGrassTop;

    @Inject(
        method = "getBlockTexture",
        at = @At("HEAD"),
        cancellable = true)
    private void modifyGetIcon(IBlockAccess worldIn, int x, int y, int z, int side, CallbackInfoReturnable<Icon> cir) {
        final Icon grassTexture = RenderBlocksUtils
            .getGrassTexture((Block) (Object) this, worldIn, x, y, z, side, this.iconGrassTop);
        if (grassTexture != null) {
            cir.setReturnValue(grassTexture);
        }
    }
}
