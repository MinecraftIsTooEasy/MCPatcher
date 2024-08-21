package moddedmite.mcpatcher.mixin.mcpatcherforge.ctm_cc;

import net.minecraft.Block;
import net.minecraft.RenderBlocks;
import net.minecraft.Icon;
import net.minecraft.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocksNoCC {

    @Shadow
    public IBlockAccess blockAccess;

    @Shadow
    public abstract Icon getBlockIcon(Block block, IBlockAccess access, int x, int y, int z, int side);

    @Shadow
    public abstract Icon getBlockIconFromSideAndMetadata(Block block, int side, int meta);

    @Redirect(
        method = "renderBlockFluids",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderBlocks;getBlockIconFromSideAndMetadata(Lnet/minecraft/Block;II)Lnet/minecraft/Icon;",
            ordinal = 0))
    private Icon mcpatcherforge$redirectToGetBlockIcon(RenderBlocks instance, Block block, int side, int meta,
        Block specializedBlock, int x, int y, int z) {
        return (this.blockAccess == null) ? this.getBlockIconFromSideAndMetadata(block, side, meta)
            : this.getBlockIcon(block, this.blockAccess, x, y, z, side);
    }

    @Redirect(
        method = "renderBlockFluids",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderBlocks;getBlockIconFromSideAndMetadata(Lnet/minecraft/Block;II)Lnet/minecraft/Icon;",
            ordinal = 2))
    private Icon mcpatcherforge$saveSideAndRedirectToGetBlockIcon(RenderBlocks instance, Block block, int side,
        int meta, Block specializedBlock, int x, int y, int z) {
        return (this.blockAccess == null) ? this.getBlockIconFromSideAndMetadata(block, side, meta)
            : this.getBlockIcon(block, this.blockAccess, x, y, z, side);
    }
}
