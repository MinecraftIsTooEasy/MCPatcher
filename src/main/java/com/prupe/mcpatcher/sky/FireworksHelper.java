package com.prupe.mcpatcher.sky;

import com.prupe.mcpatcher.MCLogger;
import com.prupe.mcpatcher.MCPatcherUtils;
import com.prupe.mcpatcher.mal.resource.BlendMethod;
import com.prupe.mcpatcher.mal.resource.GLAPI;
import com.prupe.mcpatcher.mal.resource.PropertiesFile;
import com.prupe.mcpatcher.mal.resource.TexturePackAPI;
import jss.notfine.config.MCPatcherForgeConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.EntityFX;
import net.minecraft.EntityFireworkOverlayFX;
import net.minecraft.EntityFireworkSparkFX;
import net.minecraft.ResourceLocation;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class FireworksHelper {
    private static final int LIT_LAYER = 3;
    private static final int EXTRA_LAYER = LIT_LAYER + 1;
    private static final ResourceLocation PARTICLE_PROPERTIES = TexturePackAPI.newMCPatcherResourceLocation("particle.properties");

    private static final MCLogger logger = MCLogger.getLogger(MCLogger.Category.BETTER_SKIES);
    private static final boolean enable = MCPatcherForgeConfig.instance().brightenFireworks;
    private static BlendMethod blendMethod;

    public static int getFXLayer(EntityFX entity) {
        if (enable && (entity instanceof EntityFireworkSparkFX || entity instanceof EntityFireworkOverlayFX)) {
            return EXTRA_LAYER;
        } else {
            return entity.getFXLayer();
        }
    }

    public static boolean skipThisLayer(boolean skip, int layer) {
        return skip || layer == LIT_LAYER || (!enable && layer > LIT_LAYER);
    }

    public static void setParticleBlendMethod(int layer, int pass, boolean setDefault) {
        if (enable && layer == EXTRA_LAYER && blendMethod != null) {
            blendMethod.applyBlending();
        } else if (setDefault) {
            GLAPI.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    static void reload() {
        PropertiesFile properties = PropertiesFile.getNonNull(logger, PARTICLE_PROPERTIES);
        String blend = properties.getString("blend." + EXTRA_LAYER, "add");
        blendMethod = BlendMethod.parse(blend);
        if (blendMethod == null) {
            properties.error("%s: unknown blend method %s", PARTICLE_PROPERTIES, blend);
        } else if (enable) {
            properties.config("using %s blending for fireworks particles", blendMethod);
        } else {
            properties.config("using default blending for fireworks particles");
        }
    }
}
// ---END EDIT---
