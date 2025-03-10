// +++START EDIT+++
package com.prupe.mcpatcher.mal.biome;

import com.prupe.mcpatcher.MCLogger;
import com.prupe.mcpatcher.MCPatcherUtils;
import com.prupe.mcpatcher.mal.resource.PropertiesFile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.BiomeGenBase;
import net.minecraft.IBlockAccess;
import net.minecraft.Minecraft;

import java.lang.reflect.Method;
import java.util.BitSet;

@Environment(EnvType.CLIENT)
public class BiomeAPI {
    private static final MCLogger logger = MCLogger.getLogger(MCLogger.Category.CUSTOM_COLORS);

    public static final int WORLD_MAX_HEIGHT = 255;
    public static final boolean isColorHeightDependent = false;

    private static boolean biomesLogged;

    private static Method getWaterColorMultiplier;
    private static BiomeGenBase lastBiome;
    private static int lastI;
    private static int lastK;

    static {
        try {
            getWaterColorMultiplier = BiomeGenBase.class.getDeclaredMethod("getWaterColorMultiplier");
            getWaterColorMultiplier.setAccessible(true);
            logger.config("forge getWaterColorMultiplier detected");
        } catch (NoSuchMethodException e) {
        }
    }
    
    private BiomeAPI() {
    }

    public static void parseBiomeList(String list, BitSet bits) {
        logBiomes();
        if (MCPatcherUtils.isNullOrEmpty(list)) {
            return;
        }
        for (String s : list.split(list.contains(",") ? "\\s*,\\s*" : "\\s+")) {
            BiomeGenBase biome = findBiomeByName(s);
            if (biome != null) {
                bits.set(biome.biomeID);
            }
        }
    }

    public static BitSet getHeightListProperty(PropertiesFile properties, String suffix) {
        int minHeight = Math.max(properties.getInt("minHeight" + suffix, 0), 0);
        int maxHeight = Math.min(properties.getInt("maxHeight" + suffix, WORLD_MAX_HEIGHT), WORLD_MAX_HEIGHT);
        String heightStr = properties.getString("heights" + suffix, "");
        if (minHeight == 0 && maxHeight == WORLD_MAX_HEIGHT && heightStr.length() == 0) {
            return null;
        } else {
            BitSet heightBits = new BitSet(WORLD_MAX_HEIGHT + 1);
            if (heightStr.length() == 0) {
                heightStr = String.valueOf(minHeight) + "-" + String.valueOf(maxHeight);
            }
            for (int i : MCPatcherUtils.parseIntegerList(heightStr, 0, WORLD_MAX_HEIGHT)) {
                heightBits.set(i);
            }
            return heightBits;
        }
    }

    public static BiomeGenBase findBiomeByName(String name) {
        logBiomes();
        if (name == null) {
            return null;
        }
        name = name.replace(" ", "");
        if (name.isEmpty()) {
            return null;
        }
        for (BiomeGenBase biome : BiomeGenBase.biomeList) {
            if (biome == null || biome.biomeName == null) {
                continue;
            }
            if (name.equalsIgnoreCase(biome.biomeName) || name.equalsIgnoreCase(biome.biomeName.replace(" ", ""))) {
                if (biome.biomeID >= 0 && biome.biomeID < BiomeGenBase.biomeList.length) {
                    return biome;
                }
            }
        }
        return null;
    }

    public static IBlockAccess getWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    public static int getBiomeIDAt(IBlockAccess blockAccess, int i, int j, int k) {
        BiomeGenBase biome = getBiomeGenAt(blockAccess, i, j, k);
        return biome == null ? BiomeGenBase.biomeList.length : biome.biomeID;
    }

    public static BiomeGenBase getBiomeGenAt(IBlockAccess blockAccess, int i, int j, int k) {
        if (lastBiome == null || i != lastI || k != lastK) {
            lastI = i;
            lastK = k;
            lastBiome = blockAccess.getBiomeGenForCoords(i, k);
        }
        return lastBiome;
    }

    public static float getTemperature(BiomeGenBase biome, int i, int j, int k) {
        return biome.getFloatTemperature();
    }

    public static float getTemperature(IBlockAccess blockAccess, int i, int j, int k) {
        return getTemperature(getBiomeGenAt(blockAccess, i, j, k), i, j, k);
    }

    public static float getRainfall(BiomeGenBase biome, int i, int j, int k) {
        return biome.getFloatRainfall();
    }

    public static float getRainfall(IBlockAccess blockAccess, int i, int j, int k) {
        return getRainfall(getBiomeGenAt(blockAccess, i, j, k), i, j, k);
    }

    public static int getGrassColor(BiomeGenBase biome, int i, int j, int k) {
        return biome.getBiomeGrassColor();
    }

    public static int getFoliageColor(BiomeGenBase biome, int i, int j, int k) {
        return biome.getBiomeFoliageColor();
    }

    public static int getWaterColorMultiplier(BiomeGenBase biome) {
        if (getWaterColorMultiplier != null) {
            try {
                return (Integer) getWaterColorMultiplier.invoke(biome);
            } catch (Throwable e) {
                e.printStackTrace();
                getWaterColorMultiplier = null;
            }
        }
        return biome == null ? 0xffffff : biome.waterColorMultiplier;
    }

    private static void logBiomes() {
        if (!biomesLogged) {
            biomesLogged = true;
            for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
                BiomeGenBase biome = BiomeGenBase.biomeList[i];
                if (biome != null) {
                    int x = (int) (255.0f * (1.0f - biome.temperature));
                    int y = (int) (255.0f * (1.0f - biome.temperature * biome.rainfall));
                    logger.config("setupBiome #%d id=%d \"%s\" %06x (%d,%d)", i, biome.biomeID, biome.biomeName, biome.waterColorMultiplier, x, y);
                }
            }
        }
    }
/*
    protected boolean isColorHeightDependent() {
        return false;
    }
    */
}
// ---END EDIT---
