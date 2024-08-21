package moddedmite.mcpatcher.mixin.mcpatcherforge.renderpass;

import net.minecraft.Block;
import net.minecraft.RenderBlocks;
import net.minecraft.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.prupe.mcpatcher.renderpass.RenderPass;

@Mixin(value = RenderBlocks.class)
public abstract class MixinRenderBlocks {

    @Redirect(
        method = { "renderBlockBed(Lnet/minecraft/Block;III)Z",
            "renderStandardBlockWithAmbientOcclusion(Lnet/minecraft/Block;IIIFFF)Z",
            "renderStandardBlockWithColorMultiplier(Lnet/minecraft/Block;IIIFFF)Z",
            "renderStandardBlockWithAmbientOcclusionPartial(Lnet/minecraft/Block;IIIFFF)Z",
            "renderBlockCactusImpl(Lnet/minecraft/Block;IIIFFF)Z",
            "renderBlockFluids" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/Block;shouldSideBeRendered(Lnet/minecraft/IBlockAccess;IIII)Z"))
    private boolean redirectShouldSideBeRendered(Block block, IBlockAccess worldIn, int x, int y, int z, int side) {
        return RenderPass.shouldSideBeRendered(block, worldIn, x, y, z, side);
    }

}
