package jss.notfine.mixins.early.mcpatcherforge.ctm_cc;

import net.minecraft.Block;
import net.minecraft.RenderBlocks;
import net.minecraft.Tessellator;
import net.minecraft.Icon;
import net.minecraft.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.prupe.mcpatcher.cc.ColorizeBlock;

@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocksNoCTM {

    @Shadow
    public IBlockAccess blockAccess;

    @Shadow
    public boolean enableAO;

    @Shadow
    public abstract Icon getBlockIconFromSideAndMetadata(Block block, int side, int meta);

    // Redirect calls to this.getBlockIcon when possible

    @Redirect(
        method = "renderBlockFluids",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderBlocks;getBlockIconFromSideAndMetadata(Lnet/minecraft/Block;II)Lnet/minecraft/Icon;",
            ordinal = 0))
    private Icon mcpatcherforge$obtainFloatsAndRedirectToGetBlockIcon(RenderBlocks instance, Block block, int side,
        int meta, Block specializedBlock, int x, int y, int z, @Share("red") LocalFloatRef red,
        @Share("green") LocalFloatRef green, @Share("blue") LocalFloatRef blue) {
        int l = block.colorMultiplier(this.blockAccess, x, y, z);
        red.set((float) (l >> 16 & 255) / 255.0F);
        blue.set((float) (l >> 8 & 255) / 255.0F);
        green.set((float) (l & 255) / 255.0F);
        return this.getBlockIconFromSideAndMetadata(block, side, meta);
    }

    // Capture needed value
    @Redirect(
        method = "renderBlockFluids",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderBlocks;getBlockIconFromSideAndMetadata(Lnet/minecraft/Block;II)Lnet/minecraft/Icon;",
            ordinal = 2))
    private Icon mcpatcherforge$saveSideAndRedirectToGetBlockIcon(RenderBlocks instance, Block block, int side,
        int meta, Block specializedBlock, int x, int y, int z, @Share("requiredSide") LocalIntRef requiredSide) {
        requiredSide.set(side);
        return this.getBlockIconFromSideAndMetadata(block, side, meta);
    }

    @Redirect(
        method = "renderBlockFluids",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/Tessellator;setColorOpaque_F(FFF)V",
            ordinal = 2))
    private void mcpatcherforge$redirectColor10(Tessellator tessellator, float red, float green, float blue,
        Block block, int x, int y, int z, @Share("requiredSide") LocalIntRef requiredSide) {
        if (!(ColorizeBlock.isSmooth = ColorizeBlock.setupBlockSmoothing(
            (RenderBlocks) (Object) this,
            block,
            this.blockAccess,
            x,
            y,
            z,
            requiredSide.get() + 6))) {
            tessellator.setColorOpaque_F(red, green, blue);
        }
    }

    @Redirect(
        method = "renderBlockFluids",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/Tessellator;setColorOpaque_F(FFF)V",
            ordinal = 1))
    private void mcpatcherforge$redirectColor9(Tessellator tessellator, float red, float green, float blue, Block block,
        int x, int y, int z, @Share("red") LocalFloatRef redLocal, @Share("green") LocalFloatRef greenLocal,
        @Share("blue") LocalFloatRef blueLocal) {
        if (!(ColorizeBlock.isSmooth = ColorizeBlock
            .setupBlockSmoothing((RenderBlocks) (Object) this, block, this.blockAccess, x, y, z, 6))) {
            tessellator.setColorOpaque_F(red * redLocal.get(), green * blueLocal.get(), blue * blueLocal.get());
        }
        if (ColorizeBlock.isSmooth) {
            this.enableAO = true;
        }
    }
}
