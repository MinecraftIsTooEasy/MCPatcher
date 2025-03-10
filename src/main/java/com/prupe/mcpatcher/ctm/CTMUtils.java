package com.prupe.mcpatcher.ctm;

import com.prupe.mcpatcher.MCLogger;
import com.prupe.mcpatcher.MCPatcherUtils;
import com.prupe.mcpatcher.mal.block.BlockAPI;
import com.prupe.mcpatcher.mal.block.BlockStateMatcher;
import com.prupe.mcpatcher.mal.block.RenderBlocksUtils;
import com.prupe.mcpatcher.mal.block.RenderPassAPI;
import com.prupe.mcpatcher.mal.resource.BlendMethod;
import com.prupe.mcpatcher.mal.resource.ResourceList;
import com.prupe.mcpatcher.mal.resource.TexturePackAPI;
import com.prupe.mcpatcher.mal.resource.TexturePackChangeHandler;
import com.prupe.mcpatcher.mal.tile.TileLoader;
import jss.notfine.config.MCPatcherForgeConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.*;

import java.util.*;

@Environment(EnvType.CLIENT)
public class CTMUtils {
    private static final MCLogger logger = MCLogger.getLogger(MCLogger.Category.CONNECTED_TEXTURES, "CTM");

    private static final boolean enableStandard = MCPatcherForgeConfig.instance().ctmStandard;
    private static final boolean enableNonStandard = MCPatcherForgeConfig.instance().ctmNonStandard;

    private static final List<ITileOverride> allOverrides = new ArrayList<ITileOverride>();
    private static final Map<Block, List<BlockStateMatcher>> blockOverrides = new IdentityHashMap<Block, List<BlockStateMatcher>>();
    private static final Map<String, List<ITileOverride>> tileOverrides = new HashMap<String, List<ITileOverride>>();
    private static TileLoader tileLoader;

    private static ITileOverride lastOverride;

    private static final TileOverrideIterator.IJK ijkIterator = newIJKIterator();
    private static final TileOverrideIterator.Metadata metadataIterator = newMetadataIterator();

    private static boolean haveBlockFace;
    private static final BlockOrientation renderBlockState = new BlockOrientation();

    static {
        try {
            Class.forName(MCPatcherUtils.RENDER_PASS_CLASS).getMethod("finish").invoke(null);
        } catch (Throwable e) {
        }

        TexturePackChangeHandler.register(new TexturePackChangeHandler(MCPatcherUtils.CONNECTED_TEXTURES, 3) {
            @Override
            public void initialize() {
            }

            @Override
            public void beforeChange() {
                RenderPassAPI.instance.clear();
                try {
                    GlassPaneRenderer.clear();
                } catch (NoClassDefFoundError e) {
                    // nothing
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                renderBlockState.clear();
                ijkIterator.clear();
                metadataIterator.clear();
                allOverrides.clear();
                blockOverrides.clear();
                tileOverrides.clear();
                lastOverride = null;
                RenderBlocksUtils.blankIcon = null;
                tileLoader = new TileLoader("textures/blocks", logger);
                RenderPassAPI.instance.refreshBlendingOptions();

                if (enableStandard || enableNonStandard) {
                    for (ResourceLocation resource : ResourceList.getInstance().listResources(TexturePackAPI.MCPATCHER_SUBDIR + "ctm", ".properties", true)) {
                        registerOverride(TileOverride.create(resource, tileLoader));
                    }
                }
                for (ResourceLocation resource : BlendMethod.getAllBlankResources()) {
                    tileLoader.preloadTile(resource, false);
                }
            }

            @Override
            public void afterChange() {
                for (ITileOverride override : allOverrides) {
                    override.registerIcons();
                }
                for (Map.Entry<Block, List<BlockStateMatcher>> entry : blockOverrides.entrySet()) {
                    for (BlockStateMatcher matcher : entry.getValue()) {
                        ITileOverride override = (ITileOverride) matcher.getData();
                        if (override.getRenderPass() >= 0) {
                            RenderPassAPI.instance.setRenderPassForBlock(entry.getKey(), override.getRenderPass());
                        }
                    }
                }
                for (List<BlockStateMatcher> overrides : blockOverrides.values()) {
                    Collections.sort(overrides, new Comparator<BlockStateMatcher>() {
                        @Override
                        public int compare(BlockStateMatcher m1, BlockStateMatcher m2) {
                            ITileOverride o1 = (ITileOverride) m1.getData();
                            ITileOverride o2 = (ITileOverride) m2.getData();
                            return o1.compareTo(o2);
                        }
                    });
                }
                for (List<ITileOverride> overrides : tileOverrides.values()) {
                    Collections.sort(overrides);
                }
                setBlankResource();
            }
        });
    }

    private static void clearBlockFace() {
        haveBlockFace = false;
    }

    public static Icon getBlockIcon(Icon icon, Block block, IBlockAccess blockAccess, int i, int j, int k, int face) {
        lastOverride = null;
        if (blockAccess != null && checkFace(face)) {
            if (!haveBlockFace) {
                renderBlockState.setBlock(block, blockAccess, i, j, k);
                renderBlockState.setFace(face);
            }
            lastOverride = ijkIterator.go(renderBlockState, icon);
            if (lastOverride != null) {
                icon = ijkIterator.getIcon();
            }
        }
        clearBlockFace();
        return lastOverride == null && skipDefaultRendering(block) ? RenderBlocksUtils.blankIcon : icon;
    }

    public static Icon getBlockIcon(Icon icon, Block block, int face, int metadata) {
        lastOverride = null;
        if (checkFace(face) && checkRenderType(block)) {
            renderBlockState.setBlockMetadata(block, metadata, face);
            lastOverride = metadataIterator.go(renderBlockState, icon);
            if (lastOverride != null) {
                icon = metadataIterator.getIcon();
            }
        }
        return icon;
    }

    public static Icon getBlockIcon(Icon icon, Block block, int face) {
        return getBlockIcon(icon, block, face, 0);
    }

    public static void reset() {
    }

    private static boolean checkFace(int face) {
        return face < 0 ? enableNonStandard : enableStandard;
    }

    private static boolean checkRenderType(Block block) {
        switch (block.getRenderType()) {
            case 11: // fence
            case 21: // fence gate
                return false;

            default:
                return true;
        }
    }

    private static boolean skipDefaultRendering(Block block) {
        return RenderPassAPI.instance.skipDefaultRendering(block);
    }

    private static void registerOverride(ITileOverride override) {
        if (override != null && !override.isDisabled()) {
            boolean registered = false;
            List<BlockStateMatcher> matchingBlocks = override.getMatchingBlocks();
            if (!MCPatcherUtils.isNullOrEmpty(matchingBlocks)) {
                for (BlockStateMatcher matcher : matchingBlocks) {
                    if (matcher == null) {
                        continue;
                    }
                    Block block = matcher.getBlock();
                    List<BlockStateMatcher> list = blockOverrides.get(block);
                    if (list == null) {
                        list = new ArrayList<BlockStateMatcher>();
                        blockOverrides.put(block, list);
                    }
                    list.add(matcher);
                    logger.fine("using %s for block %s", override, BlockAPI.getBlockName(block));
                    registered = true;
                }
            }
            Set<String> matchingTiles = override.getMatchingTiles();
            if (!MCPatcherUtils.isNullOrEmpty(matchingTiles)) {
                for (String name : matchingTiles) {
                    List<ITileOverride> list = tileOverrides.get(name);
                    if (list == null) {
                        list = new ArrayList<ITileOverride>();
                        tileOverrides.put(name, list);
                    }
                    list.add(override);
                    logger.fine("using %s for tile %s", override, name);
                    registered = true;
                }
            }
            if (registered) {
                allOverrides.add(override);
            }
        }
    }

    public static void setBlankResource() {
        RenderBlocksUtils.blankIcon = tileLoader.getIcon(RenderPassAPI.instance.getBlankResource());
    }

    public static TileOverrideIterator.IJK newIJKIterator() {
        return new TileOverrideIterator.IJK(blockOverrides, tileOverrides);
    }

    public static TileOverrideIterator.Metadata newMetadataIterator() {
        return new TileOverrideIterator.Metadata(blockOverrides, tileOverrides);
    }
}
// ---END EDIT---
