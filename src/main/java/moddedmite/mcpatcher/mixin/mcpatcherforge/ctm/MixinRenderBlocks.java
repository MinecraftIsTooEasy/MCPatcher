package moddedmite.mcpatcher.mixin.mcpatcherforge.ctm;

import net.minecraft.*;

import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.prupe.mcpatcher.ctm.CTMUtils;
import com.prupe.mcpatcher.ctm.GlassPaneRenderer;

@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks {

    @Shadow
    public IBlockAccess blockAccess;

    @Shadow
    public Icon overrideBlockTexture;

    @Shadow
    public abstract Icon getIconSafe(Icon texture);

    @Redirect(
        method = "renderBlockMinecartTrack(Lnet/minecraft/BlockRailBase;III)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderBlocks;getBlockIconFromSideAndMetadata(Lnet/minecraft/Block;II)Lnet/minecraft/Icon;"))
    private Icon modifyRenderBlockMinecartTrack(RenderBlocks instance, Block block, int side, int meta,
        BlockRailBase specializedBlock, int x, int y, int z) {
        return (this.blockAccess == null) ? this.getBlockIconFromSideAndMetadata(block, side, meta)
            : this.getBlockIcon(block, this.blockAccess, x, y, z, side);
    }

    @Redirect(
        method = {
            "renderBlockVine(Lnet/minecraft/Block;III)Z",
            "renderBlockLilyPad(Lnet/minecraft/Block;III)Z",
            "renderBlockLadder(Lnet/minecraft/Block;III)Z",
            "renderBlockTripWireSource(Lnet/minecraft/Block;III)Z",
            "renderBlockLever(Lnet/minecraft/Block;III)Z",
            "renderBlockTripWire(Lnet/minecraft/Block;III)Z"
        },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderBlocks;getBlockIconFromSide(Lnet/minecraft/Block;I)Lnet/minecraft/Icon;"))
    private Icon redirectGetBlockIconFromSide(RenderBlocks instance, Block block, int side, Block specializedBlock,
        int x, int y, int z) {
        return (this.blockAccess == null) ? this.getBlockIconFromSide(block, side)
            : this.getBlockIcon(block, this.blockAccess, x, y, z, side);
    }

    @Redirect(
        method = "renderBlockBrewingStand(Lnet/minecraft/BlockBrewingStand;III)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderBlocks;getBlockIconFromSideAndMetadata(Lnet/minecraft/Block;II)Lnet/minecraft/Icon;"))
    private Icon modifyRenderBlockBrewingStand(RenderBlocks instance, Block block, int side, int meta,
        BlockBrewingStand specializedBlock, int x, int y, int z) {
        return (this.blockAccess == null) ? this.getBlockIconFromSideAndMetadata(block, side, meta)
            : this.getBlockIcon(block, this.blockAccess, x, y, z, side);
    }

    @Redirect(
        method = "renderBlockFlowerpot(Lnet/minecraft/BlockFlowerPot;III)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderBlocks;getBlockIconFromSide(Lnet/minecraft/Block;I)Lnet/minecraft/Icon;"))
    private Icon modifyRenderBlockFlowerpot(RenderBlocks instance, Block block, int side,
        BlockFlowerPot specializedBlock, int x, int y, int z) {
        return (this.blockAccess == null) ? this.getBlockIconFromSide(block, side)
            : this.getBlockIcon(block, this.blockAccess, x, y, z, side);
    }

    @Redirect(
        method = "renderBlockAnvilRotate(Lnet/minecraft/BlockAnvil;IIIIFFFFZZI)F",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderBlocks;getBlockIconFromSideAndMetadata(Lnet/minecraft/Block;II)Lnet/minecraft/Icon;"))
    private Icon modifyRenderBlockAnvilRotate(RenderBlocks instance, Block block, int side, int meta,
        BlockAnvil specializedBlock, int x, int y, int z) {
        return (this.blockAccess == null) ? this.getBlockIconFromSideAndMetadata(block, side, meta)
            : this.getBlockIcon(block, this.blockAccess, x, y, z, side);
    }

//    @Redirect(
//        method = "renderBlockRepeater",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/RenderBlocks;getBlockIcon(Lnet/minecraft/Block;)Lnet/minecraft/Icon;"))
//    private Icon modifyRenderRedstoneDiodeMetadata(RenderBlocks instance, Block par1Block) {
//        return (this.blockAccess == null) ? this.getBlockIconFromSideAndMetadata(par1Block, side, meta)
//            : this.getBlockIcon(par1Block, this.blockAccess, x, y, z, side);
//    }

    /**
     * @author Mist475 (adapted from Paul Rupe)
     * @reason Significant deviation from Vanilla
     */
    @SuppressWarnings("DuplicatedCode")
    @Overwrite
    public boolean renderBlockPane(BlockPane block, int x, int y, int z) {
        int r = this.blockAccess.getHeight();
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, x, y, z));
        int d = block.colorMultiplier(this.blockAccess, x, y, z);
        float n = (d >> 16 & 0xFF) / 255.0f;
        float n2 = (d >> 8 & 0xFF) / 255.0f;
        float n3 = (d & 0xFF) / 255.0f;
        if (EntityRenderer.anaglyphEnable) {
            float n4 = (n * 30.0f + n2 * 59.0f + n3 * 11.0f) / 100.0f;
            float n5 = (n * 30.0f + n2 * 70.0f) / 100.0f;
            float n6 = (n * 30.0f + n3 * 70.0f) / 100.0f;
            n = n4;
            n2 = n5;
            n3 = n6;
        }
        tessellator.setColorOpaque_F(n, n2, n3);
        Icon d2;
        Icon rf;
        if (overrideBlockTexture != null) {
            d2 = this.overrideBlockTexture;
            rf = this.overrideBlockTexture;
        } else {
            int e = this.blockAccess.getBlockMetadata(x, y, z);
            d2 = ((this.blockAccess == null) ? this.getBlockIconFromSideAndMetadata(block, 0, e)
                : this.getBlockIcon(block, this.blockAccess, x, y, z, 0));
            rf = block.getSideTextureIndex();
        }
        double n7 = d2.getMinU();
        double n8 = d2.getInterpolatedU(8.0);
        double n9 = d2.getMaxU();
        double n10 = d2.getMinV();
        double n11 = d2.getMaxV();
        double n12 = rf.getInterpolatedU(7.0);
        double n13 = rf.getInterpolatedU(9.0);
        double n14 = rf.getMinV();
        double n15 = rf.getInterpolatedV(8.0);
        double n16 = rf.getMaxV();
        double n18 = x + 0.5;
        double n19 = x + 1;
        double n21 = z + 0.5;
        double n22 = z + 1;
        double n23 = x + 0.5 - 0.0625;
        double n24 = x + 0.5 + 0.0625;
        double n25 = z + 0.5 - 0.0625;
        double n26 = z + 0.5 + 0.0625;

        // Slightly different due to forge
        boolean a2 = block.canThisPaneConnectToThisBlockID(this.blockAccess.getBlockId(x, y, z - 1));
        boolean a3 = block.canThisPaneConnectToThisBlockID(this.blockAccess.getBlockId(x, y, y + 1));
        boolean a4 = block.canThisPaneConnectToThisBlockID(this.blockAccess.getBlockId(x - 1, y, z));
        boolean a5 = block.canThisPaneConnectToThisBlockID(this.blockAccess.getBlockId(x + 1, y, z));
        boolean a6 = block.shouldSideBeRendered(this.blockAccess, x, y + 1, z, 1);
        boolean a7 = block.shouldSideBeRendered(this.blockAccess, x, y - 1, z, 0);

        GlassPaneRenderer.renderThin((RenderBlocks) (Object) this, block, d2, x, y, z, a2, a3, a4, a5);
        if ((a4 && a5) || (!a4 && !a5 && !a2 && !a3)) {
            if (!GlassPaneRenderer.skipPaneRendering) {
                tessellator.addVertexWithUV(x, y + 1, n21, n7, n10);
                tessellator.addVertexWithUV(x, y, n21, n7, n11);
                tessellator.addVertexWithUV(n19, y, n21, n9, n11);
                tessellator.addVertexWithUV(n19, y + 1, n21, n9, n10);
                tessellator.addVertexWithUV(n19, y + 1, n21, n7, n10);
                tessellator.addVertexWithUV(n19, y, n21, n7, n11);
                tessellator.addVertexWithUV(x, y, n21, n9, n11);
                tessellator.addVertexWithUV(x, y + 1, n21, n9, n10);
            }
            if (a6) {
                if (!GlassPaneRenderer.skipTopEdgeRendering) {
                    tessellator.addVertexWithUV(x, y + 1 + 0.01, n26, n13, n16);
                    tessellator.addVertexWithUV(n19, y + 1 + 0.01, n26, n13, n14);
                    tessellator.addVertexWithUV(n19, y + 1 + 0.01, n25, n12, n14);
                    tessellator.addVertexWithUV(x, y + 1 + 0.01, n25, n12, n16);
                    tessellator.addVertexWithUV(n19, y + 1 + 0.01, n26, n13, n16);
                    tessellator.addVertexWithUV(x, y + 1 + 0.01, n26, n13, n14);
                    tessellator.addVertexWithUV(x, y + 1 + 0.01, n25, n12, n14);
                    tessellator.addVertexWithUV(n19, y + 1 + 0.01, n25, n12, n16);
                }
            } else {
                if (y < r - 1 && this.blockAccess.isAirBlock(x - 1, y + 1, z)
                    && !GlassPaneRenderer.skipTopEdgeRendering) {
                    tessellator.addVertexWithUV(x, y + 1 + 0.01, n26, n13, n15);
                    tessellator.addVertexWithUV(n18, y + 1 + 0.01, n26, n13, n16);
                    tessellator.addVertexWithUV(n18, y + 1 + 0.01, n25, n12, n16);
                    tessellator.addVertexWithUV(x, y + 1 + 0.01, n25, n12, n15);
                    tessellator.addVertexWithUV(n18, y + 1 + 0.01, n26, n13, n15);
                    tessellator.addVertexWithUV(x, y + 1 + 0.01, n26, n13, n16);
                    tessellator.addVertexWithUV(x, y + 1 + 0.01, n25, n12, n16);
                    tessellator.addVertexWithUV(n18, y + 1 + 0.01, n25, n12, n15);
                }
                if (y < r - 1 && this.blockAccess.isAirBlock(x + 1, y + 1, z)
                    && !GlassPaneRenderer.skipTopEdgeRendering) {
                    tessellator.addVertexWithUV(n18, y + 1 + 0.01, n26, n13, n14);
                    tessellator.addVertexWithUV(n19, y + 1 + 0.01, n26, n13, n15);
                    tessellator.addVertexWithUV(n19, y + 1 + 0.01, n25, n12, n15);
                    tessellator.addVertexWithUV(n18, y + 1 + 0.01, n25, n12, n14);
                    tessellator.addVertexWithUV(n19, y + 1 + 0.01, n26, n13, n14);
                    tessellator.addVertexWithUV(n18, y + 1 + 0.01, n26, n13, n15);
                    tessellator.addVertexWithUV(n18, y + 1 + 0.01, n25, n12, n15);
                    tessellator.addVertexWithUV(n19, y + 1 + 0.01, n25, n12, n14);
                }
            }
            if (a7) {
                if (!GlassPaneRenderer.skipBottomEdgeRendering) {
                    tessellator.addVertexWithUV(x, y - 0.01, n26, n13, n16);
                    tessellator.addVertexWithUV(n19, y - 0.01, n26, n13, n14);
                    tessellator.addVertexWithUV(n19, y - 0.01, n25, n12, n14);
                    tessellator.addVertexWithUV(x, y - 0.01, n25, n12, n16);
                    tessellator.addVertexWithUV(n19, y - 0.01, n26, n13, n16);
                    tessellator.addVertexWithUV(x, y - 0.01, n26, n13, n14);
                    tessellator.addVertexWithUV(x, y - 0.01, n25, n12, n14);
                    tessellator.addVertexWithUV(n19, y - 0.01, n25, n12, n16);
                }
            } else {
                if (y > 1 && this.blockAccess.isAirBlock(x - 1, y - 1, z)
                    && !GlassPaneRenderer.skipBottomEdgeRendering) {
                    tessellator.addVertexWithUV(x, y - 0.01, n26, n13, n15);
                    tessellator.addVertexWithUV(n18, y - 0.01, n26, n13, n16);
                    tessellator.addVertexWithUV(n18, y - 0.01, n25, n12, n16);
                    tessellator.addVertexWithUV(x, y - 0.01, n25, n12, n15);
                    tessellator.addVertexWithUV(n18, y - 0.01, n26, n13, n15);
                    tessellator.addVertexWithUV(x, y - 0.01, n26, n13, n16);
                    tessellator.addVertexWithUV(x, y - 0.01, n25, n12, n16);
                    tessellator.addVertexWithUV(n18, y - 0.01, n25, n12, n15);
                }
                if (y > 1 && this.blockAccess.isAirBlock(x + 1, y - 1, z)) {
                    if (!GlassPaneRenderer.skipBottomEdgeRendering) {
                        tessellator.addVertexWithUV(n18, y - 0.01, n26, n13, n14);
                        tessellator.addVertexWithUV(n19, y - 0.01, n26, n13, n15);
                        tessellator.addVertexWithUV(n19, y - 0.01, n25, n12, n15);
                        tessellator.addVertexWithUV(n18, y - 0.01, n25, n12, n14);
                        tessellator.addVertexWithUV(n19, y - 0.01, n26, n13, n14);
                        tessellator.addVertexWithUV(n18, y - 0.01, n26, n13, n15);
                        tessellator.addVertexWithUV(n18, y - 0.01, n25, n12, n15);
                        tessellator.addVertexWithUV(n19, y - 0.01, n25, n12, n14);
                    }
                }
            }
        } else if (a4) {
            if (!GlassPaneRenderer.skipPaneRendering) {
                tessellator.addVertexWithUV(x, y + 1, n21, n7, n10);
                tessellator.addVertexWithUV(x, y, n21, n7, n11);
                tessellator.addVertexWithUV(n18, y, n21, n8, n11);
                tessellator.addVertexWithUV(n18, y + 1, n21, n8, n10);
                tessellator.addVertexWithUV(n18, y + 1, n21, n7, n10);
                tessellator.addVertexWithUV(n18, y, n21, n7, n11);
                tessellator.addVertexWithUV(x, y, n21, n8, n11);
                tessellator.addVertexWithUV(x, y + 1, n21, n8, n10);
            }
            if (!a3 && !a2) {
                tessellator.addVertexWithUV(n18, y + 1, n26, n12, n14);
                tessellator.addVertexWithUV(n18, y, n26, n12, n16);
                tessellator.addVertexWithUV(n18, y, n25, n13, n16);
                tessellator.addVertexWithUV(n18, y + 1, n25, n13, n14);
                tessellator.addVertexWithUV(n18, y + 1, n25, n12, n14);
                tessellator.addVertexWithUV(n18, y, n25, n12, n16);
                tessellator.addVertexWithUV(n18, y, n26, n13, n16);
                tessellator.addVertexWithUV(n18, y + 1, n26, n13, n14);
            }
            if ((a6 || (y < r - 1 && this.blockAccess.isAirBlock(x - 1, y + 1, z)))
                && !GlassPaneRenderer.skipTopEdgeRendering) {
                tessellator.addVertexWithUV(x, y + 1 + 0.01, n26, n13, n15);
                tessellator.addVertexWithUV(n18, y + 1 + 0.01, n26, n13, n16);
                tessellator.addVertexWithUV(n18, y + 1 + 0.01, n25, n12, n16);
                tessellator.addVertexWithUV(x, y + 1 + 0.01, n25, n12, n15);
                tessellator.addVertexWithUV(n18, y + 1 + 0.01, n26, n13, n15);
                tessellator.addVertexWithUV(x, y + 1 + 0.01, n26, n13, n16);
                tessellator.addVertexWithUV(x, y + 1 + 0.01, n25, n12, n16);
                tessellator.addVertexWithUV(n18, y + 1 + 0.01, n25, n12, n15);
            }
            if (a7 || (y > 1 && this.blockAccess.isAirBlock(x - 1, y - 1, z))) {
                if (!GlassPaneRenderer.skipBottomEdgeRendering) {
                    tessellator.addVertexWithUV(x, y - 0.01, n26, n13, n15);
                    tessellator.addVertexWithUV(n18, y - 0.01, n26, n13, n16);
                    tessellator.addVertexWithUV(n18, y - 0.01, n25, n12, n16);
                    tessellator.addVertexWithUV(x, y - 0.01, n25, n12, n15);
                    tessellator.addVertexWithUV(n18, y - 0.01, n26, n13, n15);
                    tessellator.addVertexWithUV(x, y - 0.01, n26, n13, n16);
                    tessellator.addVertexWithUV(x, y - 0.01, n25, n12, n16);
                    tessellator.addVertexWithUV(n18, y - 0.01, n25, n12, n15);
                }
            }
        } else if (a5) {
            if (!GlassPaneRenderer.skipPaneRendering) {
                tessellator.addVertexWithUV(n18, y + 1, n21, n8, n10);
                tessellator.addVertexWithUV(n18, y, n21, n8, n11);
                tessellator.addVertexWithUV(n19, y, n21, n9, n11);
                tessellator.addVertexWithUV(n19, y + 1, n21, n9, n10);
                tessellator.addVertexWithUV(n19, y + 1, n21, n8, n10);
                tessellator.addVertexWithUV(n19, y, n21, n8, n11);
                tessellator.addVertexWithUV(n18, y, n21, n9, n11);
                tessellator.addVertexWithUV(n18, y + 1, n21, n9, n10);
            }
            if (!a3 && !a2) {
                tessellator.addVertexWithUV(n18, y + 1, n25, n12, n14);
                tessellator.addVertexWithUV(n18, y, n25, n12, n16);
                tessellator.addVertexWithUV(n18, y, n26, n13, n16);
                tessellator.addVertexWithUV(n18, y + 1, n26, n13, n14);
                tessellator.addVertexWithUV(n18, y + 1, n26, n12, n14);
                tessellator.addVertexWithUV(n18, y, n26, n12, n16);
                tessellator.addVertexWithUV(n18, y, n25, n13, n16);
                tessellator.addVertexWithUV(n18, y + 1, n25, n13, n14);
            }
            if ((a6 || (y < r - 1 && this.blockAccess.isAirBlock(x + 1, y + 1, z)))
                && !GlassPaneRenderer.skipTopEdgeRendering) {
                tessellator.addVertexWithUV(n18, y + 1 + 0.01, n26, n13, n14);
                tessellator.addVertexWithUV(n19, y + 1 + 0.01, n26, n13, n15);
                tessellator.addVertexWithUV(n19, y + 1 + 0.01, n25, n12, n15);
                tessellator.addVertexWithUV(n18, y + 1 + 0.01, n25, n12, n14);
                tessellator.addVertexWithUV(n19, y + 1 + 0.01, n26, n13, n14);
                tessellator.addVertexWithUV(n18, y + 1 + 0.01, n26, n13, n15);
                tessellator.addVertexWithUV(n18, y + 1 + 0.01, n25, n12, n15);
                tessellator.addVertexWithUV(n19, y + 1 + 0.01, n25, n12, n14);
            }
            if ((a7 || (y > 1 && this.blockAccess.isAirBlock(x + 1, y - 1, z)))
                && !GlassPaneRenderer.skipBottomEdgeRendering) {
                tessellator.addVertexWithUV(n18, y - 0.01, n26, n13, n14);
                tessellator.addVertexWithUV(n19, y - 0.01, n26, n13, n15);
                tessellator.addVertexWithUV(n19, y - 0.01, n25, n12, n15);
                tessellator.addVertexWithUV(n18, y - 0.01, n25, n12, n14);
                tessellator.addVertexWithUV(n19, y - 0.01, n26, n13, n14);
                tessellator.addVertexWithUV(n18, y - 0.01, n26, n13, n15);
                tessellator.addVertexWithUV(n18, y - 0.01, n25, n12, n15);
                tessellator.addVertexWithUV(n19, y - 0.01, n25, n12, n14);
            }
        }
        if ((a2 && a3) || (!a4 && !a5 && !a2 && !a3)) {
            if (!GlassPaneRenderer.skipPaneRendering) {
                tessellator.addVertexWithUV(n18, y + 1, n22, n7, n10);
                tessellator.addVertexWithUV(n18, y, n22, n7, n11);
                tessellator.addVertexWithUV(n18, y, z, n9, n11);
                tessellator.addVertexWithUV(n18, y + 1, z, n9, n10);
                tessellator.addVertexWithUV(n18, y + 1, z, n7, n10);
                tessellator.addVertexWithUV(n18, y, z, n7, n11);
                tessellator.addVertexWithUV(n18, y, n22, n9, n11);
                tessellator.addVertexWithUV(n18, y + 1, n22, n9, n10);
            }
            if (a6) {
                if (!GlassPaneRenderer.skipTopEdgeRendering) {
                    tessellator.addVertexWithUV(n24, y + 1 + 0.005, n22, n13, n16);
                    tessellator.addVertexWithUV(n24, y + 1 + 0.005, z, n13, n14);
                    tessellator.addVertexWithUV(n23, y + 1 + 0.005, z, n12, n14);
                    tessellator.addVertexWithUV(n23, y + 1 + 0.005, n22, n12, n16);
                    tessellator.addVertexWithUV(n24, y + 1 + 0.005, z, n13, n16);
                    tessellator.addVertexWithUV(n24, y + 1 + 0.005, n22, n13, n14);
                    tessellator.addVertexWithUV(n23, y + 1 + 0.005, n22, n12, n14);
                    tessellator.addVertexWithUV(n23, y + 1 + 0.005, z, n12, n16);
                }
            } else {
                if (y < r - 1 && this.blockAccess.isAirBlock(x, y + 1, z - 1)
                    && !GlassPaneRenderer.skipTopEdgeRendering) {
                    tessellator.addVertexWithUV(n23, y + 1 + 0.005, z, n13, n14);
                    tessellator.addVertexWithUV(n23, y + 1 + 0.005, n21, n13, n15);
                    tessellator.addVertexWithUV(n24, y + 1 + 0.005, n21, n12, n15);
                    tessellator.addVertexWithUV(n24, y + 1 + 0.005, z, n12, n14);
                    tessellator.addVertexWithUV(n23, y + 1 + 0.005, n21, n13, n14);
                    tessellator.addVertexWithUV(n23, y + 1 + 0.005, z, n13, n15);
                    tessellator.addVertexWithUV(n24, y + 1 + 0.005, z, n12, n15);
                    tessellator.addVertexWithUV(n24, y + 1 + 0.005, n21, n12, n14);
                }
                if (y < r - 1 && this.blockAccess.isAirBlock(x, y + 1, z + 1)
                    && !GlassPaneRenderer.skipTopEdgeRendering) {
                    tessellator.addVertexWithUV(n23, y + 1 + 0.005, n21, n12, n15);
                    tessellator.addVertexWithUV(n23, y + 1 + 0.005, n22, n12, n16);
                    tessellator.addVertexWithUV(n24, y + 1 + 0.005, n22, n13, n16);
                    tessellator.addVertexWithUV(n24, y + 1 + 0.005, n21, n13, n15);
                    tessellator.addVertexWithUV(n23, y + 1 + 0.005, n22, n12, n15);
                    tessellator.addVertexWithUV(n23, y + 1 + 0.005, n21, n12, n16);
                    tessellator.addVertexWithUV(n24, y + 1 + 0.005, n21, n13, n16);
                    tessellator.addVertexWithUV(n24, y + 1 + 0.005, n22, n13, n15);
                }
            }
            if (a7) {
                if (!GlassPaneRenderer.skipBottomEdgeRendering) {
                    tessellator.addVertexWithUV(n24, y - 0.005, n22, n13, n16);
                    tessellator.addVertexWithUV(n24, y - 0.005, z, n13, n14);
                    tessellator.addVertexWithUV(n23, y - 0.005, z, n12, n14);
                    tessellator.addVertexWithUV(n23, y - 0.005, n22, n12, n16);
                    tessellator.addVertexWithUV(n24, y - 0.005, z, n13, n16);
                    tessellator.addVertexWithUV(n24, y - 0.005, n22, n13, n14);
                    tessellator.addVertexWithUV(n23, y - 0.005, n22, n12, n14);
                    tessellator.addVertexWithUV(n23, y - 0.005, z, n12, n16);
                }
            } else {
                if (y > 1 && this.blockAccess.isAirBlock(x, y - 1, z - 1)
                    && !GlassPaneRenderer.skipBottomEdgeRendering) {
                    tessellator.addVertexWithUV(n23, y - 0.005, z, n13, n14);
                    tessellator.addVertexWithUV(n23, y - 0.005, n21, n13, n15);
                    tessellator.addVertexWithUV(n24, y - 0.005, n21, n12, n15);
                    tessellator.addVertexWithUV(n24, y - 0.005, z, n12, n14);
                    tessellator.addVertexWithUV(n23, y - 0.005, n21, n13, n14);
                    tessellator.addVertexWithUV(n23, y - 0.005, z, n13, n15);
                    tessellator.addVertexWithUV(n24, y - 0.005, z, n12, n15);
                    tessellator.addVertexWithUV(n24, y - 0.005, n21, n12, n14);
                }
                if (y > 1 && this.blockAccess.isAirBlock(x, y - 1, z + 1)) {
                    if (!GlassPaneRenderer.skipBottomEdgeRendering) {
                        tessellator.addVertexWithUV(n23, y - 0.005, n21, n12, n15);
                        tessellator.addVertexWithUV(n23, y - 0.005, n22, n12, n16);
                        tessellator.addVertexWithUV(n24, y - 0.005, n22, n13, n16);
                        tessellator.addVertexWithUV(n24, y - 0.005, n21, n13, n15);
                        tessellator.addVertexWithUV(n23, y - 0.005, n22, n12, n15);
                        tessellator.addVertexWithUV(n23, y - 0.005, n21, n12, n16);
                        tessellator.addVertexWithUV(n24, y - 0.005, n21, n13, n16);
                        tessellator.addVertexWithUV(n24, y - 0.005, n22, n13, n15);
                    }
                }
            }
        } else if (a2) {
            if (!GlassPaneRenderer.skipPaneRendering) {
                tessellator.addVertexWithUV(n18, y + 1, z, n7, n10);
                tessellator.addVertexWithUV(n18, y, z, n7, n11);
                tessellator.addVertexWithUV(n18, y, n21, n8, n11);
                tessellator.addVertexWithUV(n18, y + 1, n21, n8, n10);
                tessellator.addVertexWithUV(n18, y + 1, n21, n7, n10);
                tessellator.addVertexWithUV(n18, y, n21, n7, n11);
                tessellator.addVertexWithUV(n18, y, z, n8, n11);
                tessellator.addVertexWithUV(n18, y + 1, z, n8, n10);
            }
            if (!a5 && !a4) {
                tessellator.addVertexWithUV(n23, y + 1, n21, n12, n14);
                tessellator.addVertexWithUV(n23, y, n21, n12, n16);
                tessellator.addVertexWithUV(n24, y, n21, n13, n16);
                tessellator.addVertexWithUV(n24, y + 1, n21, n13, n14);
                tessellator.addVertexWithUV(n24, y + 1, n21, n12, n14);
                tessellator.addVertexWithUV(n24, y, n21, n12, n16);
                tessellator.addVertexWithUV(n23, y, n21, n13, n16);
                tessellator.addVertexWithUV(n23, y + 1, n21, n13, n14);
            }
            if ((a6 || (y < r - 1 && this.blockAccess.isAirBlock(x, y + 1, z - 1)))
                && !GlassPaneRenderer.skipTopEdgeRendering) {
                tessellator.addVertexWithUV(n23, y + 1 + 0.005, z, n13, n14);
                tessellator.addVertexWithUV(n23, y + 1 + 0.005, n21, n13, n15);
                tessellator.addVertexWithUV(n24, y + 1 + 0.005, n21, n12, n15);
                tessellator.addVertexWithUV(n24, y + 1 + 0.005, z, n12, n14);
                tessellator.addVertexWithUV(n23, y + 1 + 0.005, n21, n13, n14);
                tessellator.addVertexWithUV(n23, y + 1 + 0.005, z, n13, n15);
                tessellator.addVertexWithUV(n24, y + 1 + 0.005, z, n12, n15);
                tessellator.addVertexWithUV(n24, y + 1 + 0.005, n21, n12, n14);
            }
            if (a7 || (y > 1 && this.blockAccess.isAirBlock(x, y - 1, z - 1))) {
                if (!GlassPaneRenderer.skipBottomEdgeRendering) {
                    tessellator.addVertexWithUV(n23, y - 0.005, z, n13, n14);
                    tessellator.addVertexWithUV(n23, y - 0.005, n21, n13, n15);
                    tessellator.addVertexWithUV(n24, y - 0.005, n21, n12, n15);
                    tessellator.addVertexWithUV(n24, y - 0.005, z, n12, n14);
                    tessellator.addVertexWithUV(n23, y - 0.005, n21, n13, n14);
                    tessellator.addVertexWithUV(n23, y - 0.005, z, n13, n15);
                    tessellator.addVertexWithUV(n24, y - 0.005, z, n12, n15);
                    tessellator.addVertexWithUV(n24, y - 0.005, n21, n12, n14);
                }
            }
        } else if (a3) {
            if (!GlassPaneRenderer.skipPaneRendering) {
                tessellator.addVertexWithUV(n18, y + 1, n21, n8, n10);
                tessellator.addVertexWithUV(n18, y, n21, n8, n11);
                tessellator.addVertexWithUV(n18, y, n22, n9, n11);
                tessellator.addVertexWithUV(n18, y + 1, n22, n9, n10);
                tessellator.addVertexWithUV(n18, y + 1, n22, n8, n10);
                tessellator.addVertexWithUV(n18, y, n22, n8, n11);
                tessellator.addVertexWithUV(n18, y, n21, n9, n11);
                tessellator.addVertexWithUV(n18, y + 1, n21, n9, n10);
            }
            if (!a5 && !a4) {
                tessellator.addVertexWithUV(n24, y + 1, n21, n12, n14);
                tessellator.addVertexWithUV(n24, y, n21, n12, n16);
                tessellator.addVertexWithUV(n23, y, n21, n13, n16);
                tessellator.addVertexWithUV(n23, y + 1, n21, n13, n14);
                tessellator.addVertexWithUV(n23, y + 1, n21, n12, n14);
                tessellator.addVertexWithUV(n23, y, n21, n12, n16);
                tessellator.addVertexWithUV(n24, y, n21, n13, n16);
                tessellator.addVertexWithUV(n24, y + 1, n21, n13, n14);
            }
            if ((a6 || (y < r - 1 && this.blockAccess.isAirBlock(x, y + 1, z + 1)))
                && !GlassPaneRenderer.skipTopEdgeRendering) {
                tessellator.addVertexWithUV(n23, y + 1 + 0.005, n21, n12, n15);
                tessellator.addVertexWithUV(n23, y + 1 + 0.005, n22, n12, n16);
                tessellator.addVertexWithUV(n24, y + 1 + 0.005, n22, n13, n16);
                tessellator.addVertexWithUV(n24, y + 1 + 0.005, n21, n13, n15);
                tessellator.addVertexWithUV(n23, y + 1 + 0.005, n22, n12, n15);
                tessellator.addVertexWithUV(n23, y + 1 + 0.005, n21, n12, n16);
                tessellator.addVertexWithUV(n24, y + 1 + 0.005, n21, n13, n16);
                tessellator.addVertexWithUV(n24, y + 1 + 0.005, n22, n13, n15);
            }
            if ((a7 || (y > 1 && this.blockAccess.isAirBlock(x, y - 1, z + 1)))
                && !GlassPaneRenderer.skipBottomEdgeRendering) {
                tessellator.addVertexWithUV(n23, y - 0.005, n21, n12, n15);
                tessellator.addVertexWithUV(n23, y - 0.005, n22, n12, n16);
                tessellator.addVertexWithUV(n24, y - 0.005, n22, n13, n16);
                tessellator.addVertexWithUV(n24, y - 0.005, n21, n13, n15);
                tessellator.addVertexWithUV(n23, y - 0.005, n22, n12, n15);
                tessellator.addVertexWithUV(n23, y - 0.005, n21, n12, n16);
                tessellator.addVertexWithUV(n24, y - 0.005, n21, n13, n16);
                tessellator.addVertexWithUV(n24, y - 0.005, n22, n13, n15);
            }
        }
        return true;
    }

//    @Redirect(
//        method = "renderCrossedSquares(Lnet/minecraft/Block;III)Z",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/RenderBlocks;getBlockIconFromSideAndMetadata(Lnet/minecraft/Block;II)Lnet/minecraft/Icon;"))
//    private Icon redirectGetBlockIconFromSideAndMetadata(RenderBlocks instance, Block block, int side, int meta,
//        Block specializedBlock, int x, int y, int z) {
//        return (this.blockAccess == null) ? this.getBlockIconFromSideAndMetadata(block, side, meta)
//            : this.getBlockIcon(block, this.blockAccess, x, y, z, side);
//    }

//    @Redirect(
//        method = "renderBlockDoublePlant(Lnet/minecraft/BlockDoublePlant;III)Z",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/BlockDoublePlant;func_149888_a(ZI)Lnet/minecraft/util/Icon;"))
//    private Icon modifyRenderBlockDoublePlant(BlockDoublePlant block, boolean top, int meta,
//        BlockDoublePlant specializedBlock, int x, int y, int z) {
//        return CTMUtils.getBlockIcon(
//            block.func_149888_a(top, meta),
//            (RenderBlocks) (Object) this,
//            block,
//            this.blockAccess,
//            x,
//            y,
//            z,
//            -1);
//    }

    @Redirect(
        method = { "renderStandardBlockWithColorMultiplier(Lnet/minecraft/Block;IIIFFF)Z",
            "renderStandardBlockWithAmbientOcclusionPartial(Lnet/minecraft/Block;IIIFFF)Z",
            "renderStandardBlockWithAmbientOcclusion(Lnet/minecraft/Block;IIIFFF)Z" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/BlockGrass;getIconSideOverlay()Lnet/minecraft/Icon;",
            ordinal = 0))
    private Icon redirectGrassSideOverLay1(Block block, int x, int y, int z, float red, float green, float blue) {
        return CTMUtils.getBlockIcon(
                BlockGrass.getIconSideOverlay(),
                block,
                blockAccess,
                x,
                y,
                z,
                2);
    }

    @Redirect(
        method = { "renderStandardBlockWithColorMultiplier(Lnet/minecraft/Block;IIIFFF)Z",
            "renderStandardBlockWithAmbientOcclusionPartial(Lnet/minecraft/Block;IIIFFF)Z",
            "renderStandardBlockWithAmbientOcclusion(Lnet/minecraft/Block;IIIFFF)Z" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/BlockGrass;getIconSideOverlay()Lnet/minecraft/Icon;",
            ordinal = 1))
    private Icon redirectGrassSideOverLay2(Block block, int x, int y, int z, float red, float green, float blue) {
        return CTMUtils.getBlockIcon(
                BlockGrass.getIconSideOverlay(),
                block,
                blockAccess,
                x,
                y,
                z,
                3);
    }

    @Redirect(
        method = { "renderStandardBlockWithColorMultiplier(Lnet/minecraft/Block;IIIFFF)Z",
            "renderStandardBlockWithAmbientOcclusionPartial(Lnet/minecraft/Block;IIIFFF)Z",
            "renderStandardBlockWithAmbientOcclusion(Lnet/minecraft/Block;IIIFFF)Z" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/BlockGrass;getIconSideOverlay()Lnet/minecraft/Icon;",
            ordinal = 2))
    private Icon redirectGrassSideOverLay3(Block block, int x, int y, int z, float red, float green, float blue) {
        return CTMUtils.getBlockIcon(
                BlockGrass.getIconSideOverlay(),
                block,
                blockAccess,
                x,
                y,
                z,
                4);
    }

    @Redirect(
        method = { "renderStandardBlockWithColorMultiplier(Lnet/minecraft/Block;IIIFFF)Z",
            "renderStandardBlockWithAmbientOcclusionPartial(Lnet/minecraft/Block;IIIFFF)Z",
            "renderStandardBlockWithAmbientOcclusion(Lnet/minecraft/Block;IIIFFF)Z" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/BlockGrass;getIconSideOverlay()Lnet/minecraft/Icon;",
            ordinal = 3))
    private Icon redirectGrassSideOverLay4(Block block, int x, int y, int z, float red, float green, float blue) {
        return CTMUtils.getBlockIcon(
                BlockGrass.getIconSideOverlay(),
                block,
                blockAccess,
                x,
                y,
                z,
                5);
    }

    @Redirect(
        method = "renderBlockHopperMetadata(Lnet/minecraft/BlockHopper;IIIIZ)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderBlocks;getBlockIconFromSideAndMetadata(Lnet/minecraft/Block;II)Lnet/minecraft/Icon;"))
    private Icon modifyRenderBlockHopperMetadata(RenderBlocks instance, Block block, int side, int meta,
        BlockHopper specializedBlock, int x, int y, int z) {
        return (this.blockAccess == null) ? this.getBlockIconFromSideAndMetadata(block, side, meta)
            : this.getBlockIcon(block, this.blockAccess, x, y, z, side);
    }

//    @Redirect(
//        method = "getBlockIcon(Lnet/minecraft/Block;Lnet/minecraft/IBlockAccess;IIII)Lnet/minecraft/Icon;",
//        at = @At("TAIL"))
//    private Icon modifyGetBlockIcon(RenderBlocks instance, Icon texture, Block block, IBlockAccess blockAccess, int x,
//        int y, int z, int side) {
//        return CTMUtils.getBlockIcon(this.getIconSafe(par1Block.getBlockTexture(par2IBlockAccess, par3, par4, par5, par6)), this, par1Block, par2IBlockAccess, par3, par4, par5, par6);
//    }
//
//    @Redirect(
//        method = "getBlockIconFromSideAndMetadata(Lnet/minecraft/Block;II)Lnet/minecraft/Icon;",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/RenderBlocks;getIconSafe(Lnet/minecraft/Icon;)Lnet/minecraft/Icon;"))
//    private Icon modifyGetBlockIconFromSideAndMetadata(RenderBlocks instance, Icon texture, Block block, int side,
//        int meta) {
//        return CTMUtils
//            .getBlockIcon(this.getIconSafe(block.getIcon(side, meta)), (RenderBlocks) (Object) this, block, side, meta);
//    }
//
//    @Redirect(
//        method = "getBlockIconFromSide(Lnet/minecraft/Block;I)Lnet/minecraft/Icon;",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/RenderBlocks;getIconSafe(Lnet/minecraft/Icon;)Lnet/minecraft/Icon;"))
//    private Icon modifyGetBlockIconFromSide(RenderBlocks instance, Icon texture, Block block, int side) {
//        return CTMUtils.getBlockIcon(
//            this.getIconSafe(this.getIconSafe(block.getBlockTextureFromSide(side))),
//            (RenderBlocks) (Object) this,
//            block,
//            side);
//    }

    @Overwrite
    public Icon getBlockIcon(Block par1Block, IBlockAccess par2IBlockAccess, int par3, int par4, int par5, int par6) {
        return CTMUtils.getBlockIcon(
                this.getIconSafe(par1Block.getIcon(par6, par2IBlockAccess.getBlockMetadata(par3, par4, par5))),
                par1Block,
                par2IBlockAccess,
                par3,
                par4,
                par5,
                par6);

    }

    @Overwrite
    public Icon getBlockIconFromSideAndMetadata(Block par1Block, int par2, int par3) {
        return CTMUtils.getBlockIcon(
                this.getIconSafe(par1Block.getIcon(par2, par3)),
                par1Block,
                par2,
                par3);
    }

    @Overwrite
    public Icon getBlockIconFromSide(Block par1Block, int par2) {
        return CTMUtils.getBlockIcon(
                this.getIconSafe(par1Block.getBlockTextureFromSide(par2)),
                ReflectHelper.dyCast(this),
                par2);
    }

    @Redirect(
        method = "renderBlockFluids",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderBlocks;getBlockIconFromSideAndMetadata(Lnet/minecraft/Block;II)Lnet/minecraft/Icon;",
            ordinal = 1))
    private Icon mcpatcherforge$redirectToGetBlockIcon(RenderBlocks instance, Block block, int side, int meta,
        Block specializedBlock, int x, int y, int z) {
        return (this.blockAccess == null) ? this.getBlockIconFromSideAndMetadata(block, side, meta)
            : this.getBlockIcon(block, this.blockAccess, x, y, z, side);
    }

    @Redirect(
        method = "renderBlockFluids",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/RenderBlocks;getBlockIconFromSide(Lnet/minecraft/Block;I)Lnet/minecraft/Icon;"))
    private Icon mcpatcherforge$redirectToGetBlockIcon(RenderBlocks instance, Block block, int side,
        Block specializedBlock, int x, int y, int z) {
        return (this.blockAccess == null) ? this.getBlockIconFromSide(block, side)
            : this.getBlockIcon(block, this.blockAccess, x, y, z, side);
    }
}
